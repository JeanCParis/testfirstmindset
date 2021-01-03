package com.capgemini.testfirstmindset.account;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    private List<Account> accounts = new ArrayList<>();

    public Optional<Account> getAccountById(String id) {
        return accounts.stream()
                .filter(account -> account.getId().equals(id))
                .findFirst();
    }

    public String createAccount(AccountToCreate accountToCreate) {
        Account account = Account.from(accountToCreate);
        accounts.add(account);
        return account.getId();
    }
}
