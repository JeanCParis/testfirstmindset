package com.capgemini.testfirstmindset.account;

import com.capgemini.testfirstmindset.common.ApiErrors;
import com.capgemini.testfirstmindset.transfer.TransferDTO;
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

    public ApiErrors performTransfer(Account originatorAccount, TransferDTO transferDTO) {
        ApiErrors apiErrors = new ApiErrors();

        try {
            originatorAccount.withdraw(transferDTO.getAmount());
            Optional<Account> beneficiaryAccount = accountDao.getAccountById(transferDTO.getBeneficiary());
            if (beneficiaryAccount.isPresent()) {
                beneficiaryAccount.get().deposit(transferDTO.getAmount());
                accountDao.setBalance(originatorAccount.getId(), originatorAccount.getBalance());
                accountDao.setBalance(beneficiaryAccount.get().getId(), beneficiaryAccount.get().getBalance());
            } else {
                apiErrors.addError("unknown_beneficiary", new StringBuilder()
                        .append("Account with id ")
                        .append(transferDTO.getBeneficiary())
                        .append(" is unknown")
                        .toString());
            }
        } catch (InsufficientFundsException e) {
            apiErrors.addError("insufficient_funds", e.getMessage());
        }

        return apiErrors;
    }
}
