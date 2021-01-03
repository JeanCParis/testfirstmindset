package com.capgemini.testfirstmindset.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class AccountServiceTest {
    private AccountService accountService;

    @BeforeEach
    public void setup() {
        accountService = new AccountService();
    }

    @Test
    public void shouldReturnAccount() {
        // Arrange
        String id = accountService.createAccount(AccountToCreate.builder()
                .name("name")
                .build());

        //Act
        Optional<Account> account = accountService.getAccountById(id);

        //Assert
        assertAll(
                () -> assertThat(account).isNotEmpty(),
                () -> assertThat(account.get().getName()).isEqualTo("name")
        );
    }
}