package com.unasp.taskmanagement.domain.authentication.api.v1.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RequestAuthentication {
  @NotEmpty(message = "login is mandatory field")
  private String login;

  @NotEmpty(message = "password is mandatory field")
  private String password;
}