package com.capgemini.testfirstmindset.transfer;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class TransferDTO {
    private String beneficiary;
    private Integer amount;
}
