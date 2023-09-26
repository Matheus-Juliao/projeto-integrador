package com.unasp.taskmanagement.domain.authentication.api.v1.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SendTokenRequest {
    @NotEmpty(message = "email is mandatory field")
    private String email;
}