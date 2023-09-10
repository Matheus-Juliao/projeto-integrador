package com.unasp.taskmanagement.domain.user.api.v1.response;

import com.unasp.taskmanagement.domain.user.entity.User;
import com.unasp.taskmanagement.domain.user.enums.UserRole;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserChildResponse {
  private String userCreator;
  private String externalId;
  private String name;
  private String nickname;
  private int age;
  private UserRole role;

  public UserChildResponse converter(User user) {
    return UserChildResponse.builder()
        .externalId(user.getExternalId())
        .userCreator(user.getUserCreator())
        .name(user.getName())
        .role(user.getRole())
        .nickname(user.getLogin())
        .age(user.getAge())
        .build();
  }
}