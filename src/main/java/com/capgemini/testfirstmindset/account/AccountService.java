package com.capgemini.testfirstmindset.account;

import com.capgemini.testfirstmindset.common.ApiErrors;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {
    private AccountDTOValidator accountDTOValidator;
    private AccountDao accountDao;

    public AccountService(AccountDTOValidator accountDTOValidator, AccountDao accountDao) {
        this.accountDTOValidator = accountDTOValidator;
        this.accountDao = accountDao;
    }

    public ApiErrors checkPreConditionsForAccountCreation(AccountDTO accountDTO) {
        return accountDTOValidator.check(accountDTO);
    }

    public ApiErrors checkUniqueness(AccountDTO accountDTO) {
        ApiErrors apiErrors = new ApiErrors();

        Optional<Account> account = accountDao.getAccountByUsername(accountDTO.getUsername());
        if (account.isPresent()) {
            apiErrors.addError("conflict_username", "username already exists", "username", accountDTO.getUsername());
        }

        return apiErrors;
    }

    public String createAccount(AccountDTO accountDTO) {
        return accountDao.create(accountDTO);
    }

    public Optional<Account> getAccountById(String id) {
        return accountDao.getAccountById(id);
    }

    public ApiErrors withdrawFromAccount(Account account, int amount) {
        ApiErrors apiErrors = new ApiErrors();
        try {
            account.withdraw(amount);
            accountDao.setBalance(account.getId(), account.getBalance());
        } catch (Exception e) {
            apiErrors.addError("insufficient_funds", e.getMessage());
        }
        return apiErrors;
    }
}
