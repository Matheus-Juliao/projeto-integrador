package com.unasp.taskmanagement.domain.authentication.api.v1.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationResponse {
  private String token;

  public AuthenticationResponse converter(String token) {
    return AuthenticationResponse.builder()
        .token(token)
        .build();
  }

}