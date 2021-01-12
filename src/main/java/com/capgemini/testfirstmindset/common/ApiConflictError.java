package com.capgemini.testfirstmindset.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class ApiConflictError extends ApiError {
    private String key;
    private String value;

    public ApiConflictError(String code, String message, String key, String value) {
        super(code, message);
        this.key = key;
        this.value = value;
    }
}
