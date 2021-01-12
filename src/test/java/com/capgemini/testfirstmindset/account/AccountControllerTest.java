package com.capgemini.testfirstmindset.account;

import com.capgemini.testfirstmindset.common.ApiErrors;
import com.capgemini.testfirstmindset.withdraw.WithdrawDTOValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AccountControllerTest {
    @MockBean
    private AccountService accountService;
    @MockBean
    private WithdrawDTOValidator withdrawDTOValidator;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnOk_whenAccountExists() throws Exception {
        //Arrange
        Account existingAccount = Account.builder()
                .id("someId")
                .username("name")
                .balance(1000000000)
                .build();
        //Mock
        when(accountService.getAccountById("someId")).thenReturn(Optional.of(existingAccount));

        //Act
        mockMvc.perform(get("/accounts/{id}", "someId"))
                //Assert
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("someId")))
                .andExpect(jsonPath("$.username", is("name")))
                .andExpect(jsonPath("$.balance", is(1000000000)));
    }

    @Test
    public void shouldReturnNotFound_whenAccountDoesNotExist() throws Exception {
        //Arrange
        //Mock
        when(accountService.getAccountById("someId")).thenReturn(Optional.empty());

        //Act
        mockMvc.perform(get("/accounts/{id}", "someId"))
                //Assert
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnCreate_whenAccountToCreateIsUnique() throws Exception {
        //Arrange
        JSONObject content = new JSONObject()
                .put("username", "username")
                .put("balance", "1000");

        //Mock
        when(accountService.checkPreConditionsForAccountCreation(any())).thenReturn(new ApiErrors());
        when(accountService.checkUniqueness(any())).thenReturn(new ApiErrors());

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
        ApiErrors apiErrors = new ApiErrors();
        apiErrors.addError("code","message", "key","value");
        JSONObject content = new JSONObject()
                .put("username", "username")
                .put("balance", "1000");

        //Mock
        when(accountService.checkPreConditionsForAccountCreation(any())).thenReturn(new ApiErrors());
        when(accountService.checkUniqueness(any())).thenReturn(apiErrors);

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
        apiErrors.addError("code","message", "key","value");
        JSONObject content = new JSONObject()
                .put("balance", "1000");

        //Mock
        when(accountService.checkPreConditionsForAccountCreation(any())).thenReturn(apiErrors);

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
        JSONObject content = new JSONObject()
                .put("amount", "500");

        //Mock
        when(withdrawDTOValidator.validate(any())).thenReturn(new ApiErrors());
        when(accountService.getAccountById("id")).thenReturn(Optional.of(new Account()));
        when(accountService.withdrawFromAccount(any(), anyInt())).thenReturn(new ApiErrors());

        //Act
        mockMvc.perform(put("/accounts/id/withdraw")
                .content(content.toString())
                .contentType("application/json;charset=UTF-8"))
                //Assert
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnBadRequest_whenWithdrawFromKnownAccountWithInsufficientFunds() throws Exception {
        //Arrange
        JSONObject content = new JSONObject()
                .put("amount", "500");
        ApiErrors apiErrors = new ApiErrors();
        apiErrors.addError("insufficient_funds", "insufficient funds");

        //Mock
        when(withdrawDTOValidator.validate(any())).thenReturn(new ApiErrors());
        when(accountService.getAccountById(any())).thenReturn(Optional.of(new Account()));
        when(accountService.withdrawFromAccount(any(), anyInt())).thenReturn(apiErrors);

        //Act
        mockMvc.perform(put("/accounts/{id}/withdraw", "someId")
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

        //Mock
        when(accountService.getAccountById(any())).thenReturn(Optional.empty());

        //Act
        mockMvc.perform(put("/accounts/{id}/withdraw", "someId")
                .content(content.toString())
                .contentType("application/json;charset=UTF-8"))
                //Assert
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnBadRequest_whenInvalidAmountForWithdraw() throws Exception {
        //Arrange
        JSONObject content = new JSONObject().put("amount", 0);

        ApiErrors apiErrors = new ApiErrors();
        apiErrors.addError("invalid_amount","amount cannot be 0");

        //Mock
        when(accountService.getAccountById(any())).thenReturn(Optional.of(new Account()));
        when(withdrawDTOValidator.validate(any())).thenReturn(apiErrors);

        //Act
        mockMvc.perform(put("/accounts/{id}/withdraw", "someId")
                .content(content.toString())
                .contentType("application/json;charset=UTF-8"))
                //Assert
                .andExpect(status().isBadRequest())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(apiErrors)));
    }
}