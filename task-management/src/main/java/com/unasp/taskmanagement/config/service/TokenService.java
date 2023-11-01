package com.unasp.taskmanagement.config.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.unasp.taskmanagement.domain.user.entity.User;
import com.unasp.taskmanagement.domain.user.enums.UserRole;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.unasp.taskmanagement.exception.ForbiddenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
  @Value("${api.security.token.secret}")
  private String secret;
  public String generateToken(User user) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      return JWT.create()
          .withIssuer("task-management")
          .withSubject(user.getLogin())
          .withClaim("externalId", user.getExternalId())
          .withClaim("name", user.getName())
          .withArrayClaim("role", getRoles(user).toArray(new String[0]))
          .withExpiresAt(genExpirationDate())
          .sign(algorithm);
    } catch (JWTCreationException exception) {
      throw new RuntimeException("Erro while generating token", exception);
    }
  }

  public String validateToken(String token) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      return JWT.require(algorithm)
          .withIssuer("task-management")
          .build()
          .verify(token)
          .getSubject();
    } catch (JWTVerificationException exception) {
        return "";
    }
  }

  private Instant genExpirationDate() {
//    return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    return LocalDateTime.now().plusMinutes(2).toInstant(ZoneOffset.of("-03:00"));
  }

  private List<String> getRoles(User user) {
    if (user.getRole() == UserRole.SPONSOR) {
      return UserRole.getAllRoles()
          .stream()
          .map(UserRole::getRole) // Mapeia UserRole para a string da role
          .collect(Collectors.toList());
    } else {
      return Collections.singletonList(user.getRole().getRole());
    }
  }

}