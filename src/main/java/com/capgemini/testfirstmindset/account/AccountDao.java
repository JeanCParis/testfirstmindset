package com.capgemini.testfirstmindset.account;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static java.util.UUID.randomUUID;

@Repository
@Slf4j
public class AccountDao {
    private final JdbcTemplate jdbcTemplate;

    private static final String accountColumns = String.join(",", List.of(
            "ID",
            "USERNAME",
            "BALANCE"
    ));

    public AccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String create(AccountDTO accountDTO) throws DataAccessException {
        String accountId = randomUUID().toString();
        String query = new StringBuilder()
                .append("INSERT INTO T_ACCOUNT(")
                .append(accountColumns)
                .append(")VALUES(?,?,?)")
                .toString();

        jdbcTemplate.update(query, accountId, accountDTO.getUsername(), accountDTO.getBalance());

        return accountId;
    }

    public Optional<Account> getAccountById(String accountId) throws DataAccessException {
        String query = new StringBuilder()
                .append("SELECT ")
                .append(accountColumns)
                .append(" FROM T_ACCOUNT account")
                .append(" WHERE account.ID = ?")
                .toString();

        try {
            return Optional.of(jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(Account.class), accountId));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    public void setBalance(String accountId, int amount) throws DataAccessException {
        String query = new StringBuilder()
                .append("UPDATE T_ACCOUNT ")
                .append("SET BALANCE = ? ")
                .append("WHERE ID = ?")
                .toString();

        jdbcTemplate.update(query, amount, accountId);
    }

    public Optional<Account> getAccountByUsername(String username) throws DataAccessException {
        String query = new StringBuilder()
                .append("SELECT ")
                .append(accountColumns)
                .append(" FROM T_ACCOUNT account")
                .append(" WHERE account.USERNAME = ?")
                .toString();

        try {
            return Optional.of(jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(Account.class), username));
        } catch (Exception exception) {
            return Optional.empty();
        }
    }
}
