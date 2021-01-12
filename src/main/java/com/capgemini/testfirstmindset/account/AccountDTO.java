package com.capgemini.testfirstmindset.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AccountDTO {
    @JsonIgnore
    private String id;

    private String username;
    private int balance;
}
