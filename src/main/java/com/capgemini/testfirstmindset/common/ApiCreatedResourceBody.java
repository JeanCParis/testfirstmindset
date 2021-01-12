package com.capgemini.testfirstmindset.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiCreatedResourceBody {
    @ApiModelProperty(example = "bedff246-11c6-4a33-98aa-75b6117f55d0")
    private String id;
}