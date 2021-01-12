package com.capgemini.testfirstmindset.account;

import com.capgemini.testfirstmindset.common.ApiErrors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class AccountDTOValidatorTest {
    private AccountDTOValidator accountDTOValidator;

    @BeforeEach
    public void setUp() {
        accountDTOValidator = new AccountDTOValidator();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Pierre", "Paul", "Jacques"})
    void shouldReturnNoError_whenUsernameIsValid(String validUsername) {
        //Arrange
        AccountDTO accountDTO = AccountDTO.builder()
                .username(validUsername)
                .build();

        //Act
        ApiErrors apiErrors = accountDTOValidator.check(accountDTO);

        //Assert
        assertThat(apiErrors.hasErrors()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"P|erre", "P@ul", "Ja?ues"})
    void shouldReturnError_whenNameIsInvalid(String invalidUsername) {
        //Arrange
        AccountDTO accountDTO = AccountDTO.builder()
                .username(invalidUsername)
                .build();

        //Act
        ApiErrors apiErrors = accountDTOValidator.check(accountDTO);

        //Assert
        assertAll(
                () -> assertThat(apiErrors.hasErrors()).isTrue(),
                () -> assertThat(apiErrors.getErrors().get(0).getCode()).isEqualTo("invalid_username"),
                () -> assertThat(apiErrors.getErrors().get(0).getMessage()).isEqualTo("username is invalid")
        );
    }

    @Test
    void shouldReturnError_whenNameIsMissing() {
        //Arrange
        AccountDTO accountDTO = AccountDTO.builder().build();

        //Act
        ApiErrors apiErrors = accountDTOValidator.check(accountDTO);

        //Assert
        assertAll(
                () -> assertThat(apiErrors.hasErrors()).isTrue(),
                () -> assertThat(apiErrors.getErrors().get(0).getCode()).isEqualTo("mandatory_username"),
                () -> assertThat(apiErrors.getErrors().get(0).getMessage()).isEqualTo("username is mandatory")
        );
    }
}