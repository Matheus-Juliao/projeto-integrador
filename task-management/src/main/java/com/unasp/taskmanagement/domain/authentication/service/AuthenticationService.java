package com.unasp.taskmanagement.domain.authentication.service;

import com.unasp.taskmanagement.domain.authentication.api.v1.request.RequestAuthentication;
import com.unasp.taskmanagement.domain.authentication.api.v1.response.AuthenticationResponse;
import com.unasp.taskmanagement.domain.user.entity.User;
import org.springframework.security.authentication.AuthenticationManager;

public interface AuthenticationService {
  AuthenticationResponse login (RequestAuthentication requestAuthentication, AuthenticationManager authenticationManager);
  User getAuthenticatedUser();
}