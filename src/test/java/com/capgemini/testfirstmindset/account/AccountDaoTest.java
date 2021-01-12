package com.capgemini.testfirstmindset.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJdbcTest
@ActiveProfiles("test")
class AccountDaoTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private AccountDao accountDao;

    @BeforeEach
    public void setup() {
        accountDao = new AccountDao(jdbcTemplate, new AccountRowMapper());
    }

    @Test
    public void shouldCreateAccount() {
        // Arrange
        AccountDTO accountDTO = AccountDTO.builder()
                .username("name")
                .balance(1000)
                .build();
        //Act
        String accountId = accountDao.create(accountDTO);

        //Assert
        Optional<Account> account = accountDao.getAccountById(accountId);
        assertAll(
                () -> assertThat(account.isPresent()).isTrue(),
                () -> assertThat(account.get().getUsername()).isEqualTo("name"),
                () -> assertThat(account.get().getBalance()).isEqualTo(1000)
        );
    }

    @Test
    public void shouldReturnAccount_whenAccountWithIdExists() {
        // Arrange
        String existingAccountId = accountDao.create(AccountDTO.builder()
                .username("name")
                .balance(1000)
                .build());

        //Act
        Optional<Account> account = accountDao.getAccountById(existingAccountId);

        //Assert
        assertAll(
                () -> assertThat(account).isNotEmpty(),
                () -> assertThat(account.get().getUsername()).isEqualTo("name"),
                () -> assertThat(account.get().getBalance()).isEqualTo(1000)
        );
    }

    @Test
    public void shouldReturnEmpty_whenAccountWithUsernameIsUnknown() {
        // Arrange
        //Act
        Optional<Account> account = accountDao.getAccountByUsername("unknownUsername");

        //Assert
        assertThat(account).isEmpty();
    }

    @Test
    public void shouldSetBalance() {
        // Arrange
        String existingAccountId = accountDao.create(AccountDTO.builder()
                .username("name")
                .balance(1000)
                .build());
        //Act
        accountDao.setBalance(existingAccountId, 500);

        //Assert
        Account account = accountDao.getAccountById(existingAccountId).get();
        assertThat(account.getBalance()).isEqualTo(500);
    }

    @Test
    public void shouldReturnAccount_whenAccountWithUsernameExists() {
        // Arrange
        accountDao.create(AccountDTO.builder()
                .username("knownUsername")
                .build());

        //Act
        Optional<Account> account = accountDao.getAccountByUsername("knownUsername");

        //Assert
        assertAll(
                () -> assertThat(account).isNotEmpty(),
                () -> assertThat(account.get().getUsername()).isEqualTo("knownUsername"));
    }

    @Test
    public void shouldReturnEmpty_whenAccountWithIdIsUnknown() {
        // Arrange
        //Act
        Optional<Account> account = accountDao.getAccountById("unknownId");

        //Assert
        assertThat(account).isEmpty();
    }
}