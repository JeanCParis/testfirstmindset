package com.capgemini.testfirstmindset.account;

import com.capgemini.testfirstmindset.common.ApiErrors;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        jdbcTemplate.execute("TRUNCATE T_ACCOUNT;");
    }

    @Test
    public void shouldReturnOk_whenAccountExists() throws Exception {
        //Arrange
        String accountId = accountDao.create(AccountDTO.builder().username("name").balance(1000).build());

        //Act
        mockMvc.perform(get("/accounts/{id}", accountId))
        //Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(accountId)))
                .andExpect(jsonPath("$.username", is("name")))
                .andExpect(jsonPath("$.balance", is(1000)));
    }

    @Test
    public void shouldReturnNotFound_whenAccountDoesNotExist() throws Exception {
        //Arrange
        //Act
        mockMvc.perform(get("/accounts/id"))
        //Assert
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnCreate_whenAccountToCreateIsUnique() throws Exception {
        //Arrange
        JSONObject content = new JSONObject()
                .put("username", "username")
                .put("balance", "1000");

        //Act
        mockMvc.perform(post("/accounts")
                .content(content.toString())
                .contentType("application/json;charset=UTF-8"))
                //Assert
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldReturnConflict_whenAccountToCreateIsNotUnique() throws Exception {
        //Arrange
        accountDao.create(AccountDTO.builder().username("username").balance(1000).build());

        ApiErrors apiErrors = new ApiErrors();
        apiErrors.addError("conflict_username","username already exists", "username","username");

        JSONObject content = new JSONObject()
                .put("username", "username")
                .put("balance", "1000");

        //Act
        mockMvc.perform(post("/accounts")
                .content(content.toString())
                .contentType("application/json;charset=UTF-8"))
                //Assert
                .andExpect(status().isConflict())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(apiErrors)));
    }

    @Test
    public void shouldReturnBadRequest_whenMissingFieldsForAccountToCreate() throws Exception {
        //Arrange
        ApiErrors apiErrors = new ApiErrors();
        apiErrors.addError("mandatory_username","username is mandatory");
        JSONObject content = new JSONObject()
                .put("balance", "1000");

        //Act
        mockMvc.perform(post("/accounts")
                .content(content.toString())
                .contentType("application/json;charset=UTF-8"))
        //Assert
                .andExpect(status().isBadRequest())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(apiErrors)));
    }

    @Test
    public void shouldReturnOk_whenWithdrawFromKnownAccountWithSufficientFunds() throws Exception {
        //Arrange
        String accountId = accountDao.create(AccountDTO.builder().username("username").balance(1000).build());

        JSONObject content = new JSONObject()
                .put("amount", "500");

        //Act
        mockMvc.perform(put("/accounts/{id}/withdraw", accountId)
                .content(content.toString())
                .contentType("application/json;charset=UTF-8"))
        //Assert
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnBadRequest_whenWithdrawFromKnownAccountWithInsufficientFunds() throws Exception {
        //Arrange
        String accountId = accountDao.create(AccountDTO.builder().username("username").balance(400).build());

        JSONObject content = new JSONObject()
                .put("amount", "500");
        ApiErrors apiErrors = new ApiErrors();
        apiErrors.addError("insufficient_funds", "Insufficient funds : withdraw of 500 requested while only 400 available");

        //Act
        mockMvc.perform(put("/accounts/{id}/withdraw", accountId)
                .content(content.toString())
                .contentType("application/json;charset=UTF-8"))
                //Assert
                .andExpect(status().isBadRequest())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(apiErrors)));
    }

    @Test
    public void shouldReturnNotFound_whenWithdrawFromUnknownAccount() throws Exception {
        //Arrange
        JSONObject content = new JSONObject()
                .put("amount", "500");

        //Act
        mockMvc.perform(put("/accounts/{id}/withdraw", "unknownId")
                .content(content.toString())
                .contentType("application/json;charset=UTF-8"))
                //Assert
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -100})
    public void shouldReturnBadRequest_whenInvalidAmountForWithdraw(int amount) throws Exception {
        //Arrange
        String accountId = accountDao.create(AccountDTO.builder().username("username").balance(400).build());

            JSONObject content = new JSONObject().put("amount", amount);

        ApiErrors apiErrors = new ApiErrors();
        apiErrors.addError("invalid_amount", "amount must be strictly positive");

        //Act
        mockMvc.perform(put("/accounts/{id}/withdraw", accountId)
                .content(content.toString())
                .contentType("application/json;charset=UTF-8"))
                //Assert
                .andExpect(status().isBadRequest())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(apiErrors)));
    }
}
