package com.capgemini.testfirstmindset.common;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Getter
@NoArgsConstructor
@ToString
public class ApiErrors {
    private List<ApiError> errors = new ArrayList<>();

    public void addError(String code, String message) {
        this.errors.add(new ApiError(code, message));
    }

    public void addError(String code, String message, String key, String value) {
        errors.add(new ApiConflictError(code, message, key, value));
    }

    public boolean hasErrors() {
        return isNotEmpty(errors);
    }
}

