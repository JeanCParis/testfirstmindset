package com.capgemini.testfirstmindset.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AccountController.class)
@ActiveProfiles("test")
class AccountControllerTest {
    @MockBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    private AccountController accountController;

    @BeforeEach
    public void setup() {
        accountController = new AccountController(accountService);
    }

    @Test
    public void shouldReturnOk_whenAccountExists() throws Exception {
        // Arrange
        Account existingAccount = Account.builder()
                .id("id")
                .name("name")
                .balance(1000000000)
                .build();

        when(accountService.getAccountById("id")).thenReturn(Optional.of(existingAccount));

        //Act
        mockMvc.perform(get("/accounts/id"))
        //Assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is("id")))
            .andExpect(jsonPath("$.name", is("name")))
            .andExpect(jsonPath("$.balance", is(1000000000)));
    }

    @Test
    public void shouldReturnNotFound_whenAccountDoesNotExist() {
        // Arrange
        when(accountService.getAccountById("id")).thenReturn(Optional.empty());

        //Act
        ResponseEntity<Account> responseEntity = accountController.getAccountById("id");

        //Assert
        assertAll(
                () -> assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                () -> assertThat(responseEntity.getBody()).isNull()
        );
    }
}