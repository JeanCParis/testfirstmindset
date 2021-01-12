package com.capgemini.testfirstmindset.account;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

class AccountTest {

    @Test
    public void shouldCreateAccount() {
        // Arrange
        AccountDTO accountDTO = AccountDTO.builder()
                .username("name")
                .balance(1000)
                .build();

        //Act
        Account account = Account.from(accountDTO);

        //Assert
        assertAll(
                () -> assertThat(account.getBalance()).isEqualTo(1000),
                () ->  assertThat(account.getUsername()).isEqualTo("name")
        );
    }

    @Test
    public void shouldUpdateBalance_whenWithdrawingAndSufficientFunds() throws InsufficientFundsException {
        // Arrange
        Account account = Account.builder()
                .balance(1000)
                .build();

        //Act
        account.withdraw(500);

        //Assert
        assertThat(account.getBalance()).isEqualTo(500);
    }

    @Test
    public void shouldThrowException_whenWithdrawingAndInsufficientFunds() {
        // Arrange
        Account account = Account.builder()
                .balance(1000)
                .build();

        //Act
        //Assert
        assertThatExceptionOfType(InsufficientFundsException.class).isThrownBy(() -> account.withdraw(1500))
                .withMessage("Insufficient funds : withdraw of 1500 requested while only 1000 available");
    }


    @Test
    public void shouldUpdateBalance_whenWithDeposit() {
        // Arrange
        Account account = Account.builder()
                .balance(500)
                .build();

        //Act
        account.deposit(500);

        //Assert
        assertThat(account.getBalance()).isEqualTo(1000);
    }
}