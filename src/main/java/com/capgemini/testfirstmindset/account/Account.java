package com.capgemini.testfirstmindset.account;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
public class Account {
    private String id;
    private String name;
    private int balance;

    public static Account from(AccountToCreate accountToCreate) {
        return Account.builder()
                .id(UUID.randomUUID().toString())
                .name(accountToCreate.getName())
                .build();
    }

    public void deposit(int amount) {
        balance += amount;
    }

    public void withdraw(int amount) throws InsufficientFundsException {
        if(amount > balance) {
            throw new InsufficientFundsException(balance, amount);
        }
        balance -= amount;
    }
}
