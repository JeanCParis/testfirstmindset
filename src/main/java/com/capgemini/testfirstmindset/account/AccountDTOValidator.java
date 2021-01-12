package com.capgemini.testfirstmindset.account;

import com.capgemini.testfirstmindset.common.ApiErrors;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

import static org.apache.logging.log4j.util.Strings.isBlank;

@Service
public class AccountDTOValidator {
    private static final String NAME_NON_ACCEPTED_VALUES = "[^-A-Za-z0-9’'. ]";

    public ApiErrors check(AccountDTO accountDTO) {
        ApiErrors errors = new ApiErrors();

        checkUsername(accountDTO.getUsername(), errors);

        return errors;
    }

    private void checkUsername(String name, ApiErrors errors) {
        if (isBlank(name)) {
            errors.addError("mandatory_username", "username is mandatory");
        } else if (hasUnexpectedSpecialCharacters(name)) {
            errors.addError("invalid_username", "username is invalid");
        }
    }

    private boolean hasUnexpectedSpecialCharacters(String name) {
        return Pattern.compile(NAME_NON_ACCEPTED_VALUES).matcher(name).find();
    }
}
