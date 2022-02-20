package com.capgemini.testfirstmindset.transfer;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferDTO {
    private String beneficiary;
    private Integer amount;
}
