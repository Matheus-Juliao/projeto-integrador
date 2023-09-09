package com.unasp.taskmanagement.domain.user.api.v1.request;

import com.unasp.taskmanagement.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserChildRequest {
  @NotEmpty(message = "userCreator is mandatory field")
  @Size(max = 45, message = "userCreator field has a maximum size of 45 characters")
  private String userCreator;

  @NotEmpty(message = "name is mandatory field")
  @Size(max = 100, message = "name field has a maximum size of 100 characters")
  private String name;

  @NotEmpty(message = "nickname is mandatory field")
  @Size(max = 100, message = "nickname field has a maximum size of 100 characters")
  private String nickname;

  @NotEmpty(message = "password is mandatory field")
  @Size(min = 5, max = 20, message = "password field has size minimum of 5 and a maximum of 20 characters")
  private String password;

  @NotEmpty(message = "age is mandatory field")
  private int age;

  public User converter() {
    return User.builder()
        .externalId(UUID.randomUUID().toString())
        .name(name)
        .login(nickname)
        .password(password)
        .age(age)
        .active(true)
        .createdDate(LocalDateTime.now(ZoneId.of("UTC")))
        .build();
  }
}