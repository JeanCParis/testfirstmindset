package com.capgemini.testfirstmindset.withdraw;

import com.capgemini.testfirstmindset.common.ApiErrors;
import org.springframework.stereotype.Service;

@Service
public class WithdrawDTOValidator {

    public ApiErrors validate(WithdrawDTO withdrawDTO) {
        ApiErrors errors = new ApiErrors();

        if (withdrawDTO.getAmount() <= 0) {
            errors.addError("invalid_amount", "amount must be strictly positive");
        }

        return errors;
    }
}
