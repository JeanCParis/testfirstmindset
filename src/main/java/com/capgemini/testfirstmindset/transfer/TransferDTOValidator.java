package com.capgemini.testfirstmindset.transfer;

import com.capgemini.testfirstmindset.common.ApiErrors;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;
import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
public class TransferDTOValidator {
    public ApiErrors validate(TransferDTO transferDTO) {
        ApiErrors apiErrors = new ApiErrors();

        if (isBlank(transferDTO.getBeneficiary())) {
            apiErrors.addError("mandatory_beneficiary", "Beneficiary is mandatory");
        }

        if (isNull(transferDTO.getAmount())) {
            apiErrors.addError("mandatory_amount", "Amount is mandatory");
        } else if (transferDTO.getAmount() <= 0) {
            apiErrors.addError("invalid_amount", "Amount must be strictly positive");
        }

        return apiErrors;
    }
}
