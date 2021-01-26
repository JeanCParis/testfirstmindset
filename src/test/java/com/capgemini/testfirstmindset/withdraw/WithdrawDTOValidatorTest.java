package com.capgemini.testfirstmindset.withdraw;

import com.capgemini.testfirstmindset.common.ApiErrors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class WithdrawDTOValidatorTest {
    private WithdrawDTOValidator withdrawDTOValidator;

    @BeforeEach
    public void setUp() {
        withdrawDTOValidator = new WithdrawDTOValidator();
    }

    @Test
    void shouldReturnNoError_whenAmountIsStrictlyPositive() {
        //Arrange
        WithdrawDTO withdrawDTO = new WithdrawDTO(100);

        //Act
        ApiErrors apiErrors = withdrawDTOValidator.validate(withdrawDTO);

        //Assert
        assertThat(apiErrors.hasErrors()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -100})
    void shouldReturnError_whenAmountIsSmallerOrEqualToZero(int amount) {
        //Arrange
        WithdrawDTO withdrawDTO = new WithdrawDTO(amount);

        //Act
        ApiErrors apiErrors = withdrawDTOValidator.validate(withdrawDTO);

        //Assert
        assertAll(
                () -> assertThat(apiErrors.hasErrors()).isTrue(),
                () -> assertThat(apiErrors.getErrors().get(0).getCode()).isEqualTo("invalid_amount"),
                () -> assertThat(apiErrors.getErrors().get(0).getMessage()).isEqualTo("amount must be strictly positive")
        );
    }
}