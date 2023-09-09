package com.unasp.taskmanagement.config.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ErrorValidationDTO {
    private Integer code;
    private String field;
    private String message;
}