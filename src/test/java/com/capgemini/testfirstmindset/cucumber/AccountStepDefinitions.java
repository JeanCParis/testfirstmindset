package com.capgemini.testfirstmindset.cucumber;

import com.capgemini.testfirstmindset.account.Account;
import com.capgemini.testfirstmindset.transfer.TransferDTO;
import com.capgemini.testfirstmindset.withdraw.WithdrawDTO;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
public class AccountStepDefinitions {
    @LocalServerPort
    int port;

    @Autowired
    private JdbcTemplate template;
    @Autowired
    public TestRestTemplate testRestTemplate;

    @Given("Account with id {string} has balance of {int}")
    public void set_account_with_balance(String id, int balance) {
        createAccount(id, "name", balance);
    }

    @When("Account with id {string} withdraws {int}")
    public void withdraw_amount(String id, int amount) {
        testRestTemplate.put("http://localhost:{port}/accounts/{id}/withdraw", new WithdrawDTO(amount), port, id);
    }

    @When("Account with id {string} transfers {int} to account with id {string}")
    public void withdraw_amount(String sourceId, int amount, String destinationId) {
        testRestTemplate.put("http://localhost:{port}/accounts/{id}/transfer", new TransferDTO(destinationId, amount), port, sourceId);
    }

    @Then("Account with id {string} balance is {int}")
    public void the_account_has_balance(String id, int expectedBalance) {

        ResponseEntity<Account> responseEntity = testRestTemplate.getForEntity("http://localhost:{port}/accounts/{id}", Account.class, port, id);

        Account account = responseEntity.getBody();
        assertThat(account.getBalance()).isEqualTo(expectedBalance);
    }

    private void createAccount(String id, String username, int balance) {
        final String accountColumns = String.join(",", List.of(
                "ID",
                "USERNAME",
                "BALANCE"
        ));

        String query = new StringBuilder()
                .append("INSERT INTO T_ACCOUNT(")
                .append(accountColumns)
                .append(")VALUES(?,?,?)")
                .toString();

        Object[] buildParametersForSave = new Object[]{
                id,
                username,
                balance
        };

        template.update(query, buildParametersForSave);
    }
}