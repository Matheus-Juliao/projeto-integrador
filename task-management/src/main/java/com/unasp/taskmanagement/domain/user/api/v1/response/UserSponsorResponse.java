package com.unasp.taskmanagement.domain.user.api.v1.response;

import com.unasp.taskmanagement.domain.user.entity.User;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserSponsorResponse {
  private String externalId;
  private String name;
  private String email;

  public UserSponsorResponse converter(User user) {
    return UserSponsorResponse.builder()
        .externalId(user.getExternalId())
        .name(user.getName())
        .email(user.getLogin())
        .build();
  }
}