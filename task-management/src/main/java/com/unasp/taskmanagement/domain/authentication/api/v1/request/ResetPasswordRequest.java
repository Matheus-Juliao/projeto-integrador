package com.unasp.taskmanagement.domain.authentication.api.v1.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResetPasswordRequest {
    @NotEmpty(message = "email is mandatory field")
    @Size(max = 100, message = "email field has a maximum size of 100 characters")
    @Email(message = "email invalid")
    private String email;

    @NotEmpty(message = "token is mandatory field")
    @Size(max = 5, message = "token field has a maximum size of 5 characters")
    private String token;

    @NotEmpty(message = "newPassword is mandatory field")
    @Size(min = 5, max = 20, message = "newPassword field has size minimum of 5 and a maximum of 20 characters")
    private String newPassword;
}