package com.capgemini.testfirstmindset.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    private String id;

    private String username;
    private int balance;

    public static Account from(AccountDTO accountDTO) {
        return Account.builder()
                .username(accountDTO.getUsername())
                .balance(accountDTO.getBalance())
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
