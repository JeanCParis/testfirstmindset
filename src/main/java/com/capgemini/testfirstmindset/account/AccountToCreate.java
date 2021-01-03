package com.capgemini.testfirstmindset.account;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AccountToCreate {
    private String name;
}
