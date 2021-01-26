package com.capgemini.testfirstmindset.transfer;

import com.capgemini.testfirstmindset.common.ApiErrors;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class TransferDTOValidatorTest {

    private TransferDTOValidator transferDTOValidator = new TransferDTOValidator();

    @Test
    public void shouldReturnEmptyApiErrors_whenContainsAllFields() {
        //Arrange
        TransferDTO validTransferDTO = TransferDTO.builder()
                .beneficiary("someId")
                .amount(100)
                .build();
        //Act
        ApiErrors apiErrors = transferDTOValidator.validate(validTransferDTO);

        //Assert
        assertThat(apiErrors.hasErrors()).isFalse();
    }

    @Test
    public void shouldReturnErrors_whenMissingFields() {
        //Arrange
        TransferDTO invalidTransferDTO = TransferDTO.builder().build();
        //Act
        ApiErrors apiErrors = transferDTOValidator.validate(invalidTransferDTO);

        //Assert
        assertAll(
                () -> assertThat(apiErrors.hasErrors()).isTrue(),
                () -> assertThat(apiErrors.getErrors().size()).isEqualTo(2),
                () -> assertThat(apiErrors.getErrors().get(0).getCode()).isEqualTo("mandatory_beneficiary"),
                () -> assertThat(apiErrors.getErrors().get(0).getMessage()).isEqualTo("Beneficiary is mandatory"),
                () -> assertThat(apiErrors.getErrors().get(1).getCode()).isEqualTo("mandatory_amount"),
                () -> assertThat(apiErrors.getErrors().get(1).getMessage()).isEqualTo("Amount is mandatory")
        );
    }

    @Test
    public void shouldReturnError_whenAmountIsInvalid() {
        //Arrange
        TransferDTO invalidTransferDTO = TransferDTO.builder()
                .beneficiary("beneficiaryAcccountId")
                .amount(-100)
                .build();
        //Act
        ApiErrors apiErrors = transferDTOValidator.validate(invalidTransferDTO);

        //Assert
        assertAll(
                () -> assertThat(apiErrors.hasErrors()).isTrue(),
                () -> assertThat(apiErrors.getErrors().size()).isEqualTo(1),
                () -> assertThat(apiErrors.getErrors().get(0).getCode()).isEqualTo("invalid_amount"),
                () -> assertThat(apiErrors.getErrors().get(0).getMessage()).isEqualTo("Amount must be strictly positive")
        );
    }
}