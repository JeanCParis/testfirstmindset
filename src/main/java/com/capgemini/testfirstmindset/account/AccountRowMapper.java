package com.capgemini.testfirstmindset.account;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class AccountRowMapper implements RowMapper<Account> {

    @Override
    public Account mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Account.builder()
                .id(resultSet.getString("ID"))
                .username(resultSet.getString("USERNAME"))
                .balance(resultSet.getInt("BALANCE"))
                .build();
    }
}
