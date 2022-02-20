package com.capgemini.testfirstmindset.account;

import com.capgemini.testfirstmindset.common.ApiConflictError;
import com.capgemini.testfirstmindset.common.ApiErrors;
import com.capgemini.testfirstmindset.transfer.TransferDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountDTOValidator accountDTOValidator;
    @Mock
    private AccountDao accountDao;

    private AccountService accountService;

    @BeforeEach
    public void setup() {
        accountService = new AccountService(accountDTOValidator, accountDao);
    }

    @Test
    public void shouldCheckPreConditionsForAccountCreation() {
        // Arrange
        AccountDTO accountDTO = AccountDTO.builder().build();

        //Act
        accountService.checkPreConditionsForAccountCreation(accountDTO);

        //Assert
        verify(accountDTOValidator).check(accountDTO);
    }

    @Test
    public void shouldReturnError_whenAccountIsNotUnique() {
        // Arrange
        Account existingAccount = Account.builder()
                .id("id")
                .username("existingName")
                .build();

        AccountDTO accountDTO = AccountDTO.builder()
                .username("existingName")
                .build();

        //Mock
        when(accountDao.getAccountByUsername("existingName")).thenReturn(Optional.of(existingAccount));

        //Act
        ApiErrors errors = accountService.checkUniqueness(accountDTO);

        //Assert

        assertAll(
                () -> assertThat(errors.hasErrors()).isTrue(),
                () -> assertThat(errors.getErrors().size()).isEqualTo(1),
                () -> assertThat(errors.getErrors().get(0).getCode()).isEqualTo("conflict_username"),
                () -> assertThat(errors.getErrors().get(0).getMessage()).isEqualTo("username already exists"),
                () -> assertThat(((ApiConflictError)errors.getErrors().get(0)).getKey()).isEqualTo("username"),
                () -> assertThat(((ApiConflictError)errors.getErrors().get(0)).getValue()).isEqualTo("existingName")
        );
    }

    @Test
    public void shouldReturnNoError_whenAccountIsUnique() {
        // Arrange
        AccountDTO accountDTO = AccountDTO.builder()
                .username("uniqueName")
                .build();
        //Mock
        when(accountDao.getAccountByUsername("uniqueName")).thenReturn(Optional.empty());

        //Act
        ApiErrors errors = accountService.checkUniqueness(accountDTO);

        //Assert
        assertThat(errors.hasErrors()).isFalse();
    }

    @Test
    public void shouldCreateAccount() {
        // Arrange
        AccountDTO accountDTO = AccountDTO.builder()
                .username("name")
                .balance(1000)
                .build();
        //Act
        accountService.createAccount(accountDTO);

        //Assert
        verify(accountDao).create(accountDTO);
    }

    @Test
    public void shouldReturnAccount_whenAccountIdIsKnown() {
        // Arrange
        //Act
        accountService.getAccountById("knownId");

        //Assert
        verify(accountDao).getAccountById("knownId");
    }

    @Test
    public void shouldWithdrawAmount_whenSufficientFunds() throws InsufficientFundsException {
        // Arrange
        //Mock
        Account accountWithSufficientFunds = mock(Account.class);
        when(accountWithSufficientFunds.getId()).thenReturn("knownId");
        when(accountWithSufficientFunds.getBalance()).thenReturn(600);

        //Act
        ApiErrors errors = accountService.withdrawFromAccount(accountWithSufficientFunds, 400);

        //Assert
        assertAll(
                () -> assertThat(errors.hasErrors()).isFalse(),
                () -> verify(accountDao).setBalance("knownId", 600)
        );

    }

    @Test
    public void shouldThrowException_whenInsufficientFunds() throws InsufficientFundsException {
        // Arrange
        //Mock
        Account accountWithInsufficientFunds = mock(Account.class);
        InsufficientFundsException insufficientFundsException = mock(InsufficientFundsException.class);
        when(insufficientFundsException.getMessage()).thenReturn("message");
        doThrow(insufficientFundsException).when(accountWithInsufficientFunds).withdraw(400);

        //Act
        ApiErrors errors = accountService.withdrawFromAccount(accountWithInsufficientFunds, 400);
        //Assert
        assertAll(
                () -> assertThat(errors.hasErrors()).isTrue(),
                () -> assertThat(errors.getErrors().get(0).getCode()).isEqualTo("insufficient_funds"),
                () -> assertThat(errors.getErrors().get(0).getMessage()).isEqualTo("message")
        );
    }

    @Test
    public void shouldReturnNoError_whenSufficientFunds_andBeneficiaryExists() {
        //Arrange
        Account originatorAccount = Account.builder()
                .id("originatorAccountId")
                .username("originator")
                .balance(1000)
                .build();

        TransferDTO transferDTO = TransferDTO.builder()
                .beneficiary("beneficiaryAccountId")
                .amount(100)
                .build();

        Account beneficiaryAccount = Account.builder()
                .id("beneficiaryAccountId")
                .username("beneficiary")
                .balance(1000)
                .build();
        //Mock
        when(accountService.getAccountById("beneficiaryAccountId")).thenReturn(Optional.of(beneficiaryAccount));

        //Act
        ApiErrors apiErrors = accountService.performTransfer(originatorAccount, transferDTO);

        //Assert
        assertAll(
                () -> assertThat(apiErrors.hasErrors()).isFalse(),
                () -> accountDao.setBalance("originatorAccountId", 900),
                () -> accountDao.setBalance("originatorAccountId", 1100)
        );
    }

    @Test
    public void shouldReturnError_whenInsufficientFunds() {
        //Arrange
        Account originatorAccount = Account.builder()
                .id("originatorAccountId")
                .username("originator")
                .balance(1000)
                .build();

        TransferDTO transferDTO = TransferDTO.builder()
                .beneficiary("beneficiaryAccountId")
                .amount(1001)
                .build();

        ApiErrors expectedErrors = new ApiErrors();
        expectedErrors.addError("insufficient_funds", "Insufficient funds : withdraw of 1001 requested while only 1000 available");

        //Act
        ApiErrors apiErrors = accountService.performTransfer(originatorAccount, transferDTO);

        //Assert
        assertAll(
                () -> assertThat(apiErrors.hasErrors()).isTrue(),
                () -> assertThat(apiErrors.getErrors().size()).isEqualTo(1),
                () -> assertThat(apiErrors).isEqualTo(expectedErrors),
                () -> verify(accountDao, never()).setBalance(any(), anyInt())
        );
    }

    @Test
    public void shouldReturnError_whenBeneficiaryIsUnknown() {
        //Arrange
        Account originatorAccount = Account.builder()
                .id("originatorAccountId")
                .username("originator")
                .balance(1000)
                .build();

        TransferDTO transferDTO = TransferDTO.builder()
                .beneficiary("unknownBeneficiaryAccountId")
                .amount(1000)
                .build();

        ApiErrors expectedErrors = new ApiErrors();
        expectedErrors.addError("unknown_beneficiary", "Account with id unknownBeneficiaryAccountId is unknown");

        //Mock
        when(accountDao.getAccountById("unknownBeneficiaryAccountId")).thenReturn(Optional.empty());

        //Act
        ApiErrors apiErrors = accountService.performTransfer(originatorAccount, transferDTO);

        //Assert
        assertAll(
                () -> assertThat(apiErrors.hasErrors()).isTrue(),
                () -> assertThat(apiErrors.getErrors().size()).isEqualTo(1),
                () -> assertThat(apiErrors).isEqualTo(expectedErrors),
                () -> verify(accountDao, never()).setBalance(any(), anyInt())
        );
    }
}