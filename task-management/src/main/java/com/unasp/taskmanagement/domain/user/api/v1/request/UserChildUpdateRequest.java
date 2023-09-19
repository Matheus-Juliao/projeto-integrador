package com.unasp.taskmanagement.domain.user.api.v1.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserChildUpdateRequest {
  @NotEmpty(message = "name is mandatory field")
  @Size(max = 100, message = "name field has a maximum size of 100 characters")
  private String name;

  @NotEmpty(message = "nickname is mandatory field")
  @Size(max = 100, message = "nickname field has a maximum size of 100 characters")
  private String nickname;

  @Size(min = 5, max = 20, message = "password field has size minimum of 5 and a maximum of 20 characters")
  private String password;

  @NotNull(message = "age is mandatory field")
  private Integer age;
}