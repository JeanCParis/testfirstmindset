package com.capgemini.testfirstmindset.cucumber;

import com.capgemini.testfirstmindset.account.Account;
import com.capgemini.testfirstmindset.account.InsufficientFundsException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class LibraryTest extends CucumberStepDefinitions {
    private Account account;

    @Given("Account has balance of {int}")
    public void set_account_with_balance(int balance) {
        account = Account.builder()
                .balance(balance)
                .build();
    }

    @When("{int} is withdrawn")
    public void withdraw_amount(int amount) throws InsufficientFundsException {
        account.withdraw(amount);
    }

    @Then("Remaining account balance is {int}")
    public void the_account_has_balance(int expectedBalance) {
        assertThat(account.getBalance()).isEqualTo(expectedBalance);
    }
}