package com.capgemini.testfirstmindset.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ApiError {
    @ApiModelProperty(example = "mandatory_name")
    private String code;
    @ApiModelProperty(example = "Name is mandatory")
    private String message;
}

