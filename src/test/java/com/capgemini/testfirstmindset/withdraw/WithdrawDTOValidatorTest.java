package com.capgemini.testfirstmindset.withdraw;

import com.capgemini.testfirstmindset.common.ApiErrors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class WithdrawDTOValidatorTest {
    private WithdrawDTOValidator withdrawDTOValidator;

    @BeforeEach
    public void setUp() {
        withdrawDTOValidator = new WithdrawDTOValidator();
    }

    @Test
    void shouldReturnNoError_whenAmountIsNotZero() {
        //Arrange
        WithdrawDTO withdrawDTO = new WithdrawDTO(100);

        //Act
        ApiErrors apiErrors = withdrawDTOValidator.validate(withdrawDTO);

        //Assert
        assertThat(apiErrors.hasErrors()).isFalse();
    }

    @Test
    void shouldReturnError_whenAmountIsZero() {
        //Arrange
        WithdrawDTO withdrawDTO = new WithdrawDTO(0);

        //Act
        ApiErrors apiErrors = withdrawDTOValidator.validate(withdrawDTO);

        //Assert
        assertAll(
                () -> assertThat(apiErrors.hasErrors()).isTrue(),
                () -> assertThat(apiErrors.getErrors().get(0).getCode()).isEqualTo("invalid_amount"),
                () -> assertThat(apiErrors.getErrors().get(0).getMessage()).isEqualTo("amount cannot be 0")
        );
    }
}