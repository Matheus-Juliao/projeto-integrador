package com.unasp.taskmanagement.domain.user.api.v1.response;

import com.unasp.taskmanagement.domain.user.entity.User;
import com.unasp.taskmanagement.domain.user.enums.UserRole;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserChildResponse {
  private String externalId;
  private String name;
  private String nickname;
  private int age;
  private UserRole role;

  public UserChildResponse converter(User user) {
    return UserChildResponse.builder()
        .externalId(user.getExternalId())
        .name(user.getName())
        .nickname(user.getLogin())
        .age(user.getAge())
        .role(user.getRole())
        .build();
  }
}