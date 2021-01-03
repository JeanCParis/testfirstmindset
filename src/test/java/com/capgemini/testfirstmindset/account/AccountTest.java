package com.capgemini.testfirstmindset.account;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

class AccountTest {

    @Test
    public void shouldCreateAccountWithNullBalance() {
        // Arrange
        AccountToCreate accountToCreate = AccountToCreate.builder()
                .name("name")
                .build();

        //Act
        Account account = Account.from(accountToCreate);

        //Assert
        assertAll(
                () -> assertThat(account.getBalance()).isEqualTo(0),
                () ->  assertThat(account.getName()).isEqualTo("name")
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
                .withMessage("Insufficient funds : withdraw of 1500.0 requested while only 1000.0 available");
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