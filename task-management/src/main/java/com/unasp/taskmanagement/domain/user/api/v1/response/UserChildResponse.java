package com.unasp.taskmanagement.domain.user.api.v1.response;

import com.unasp.taskmanagement.domain.user.entity.User;
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

  public UserChildResponse converter(User user) {
    return UserChildResponse.builder()
        .externalId(externalId)
        .userCreator(userCreator)
        .name(name)
        .nickname(nickname)
        .age(age)
        .build();
  }
}