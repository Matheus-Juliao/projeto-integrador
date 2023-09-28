package com.unasp.taskmanagement.domain.authentication.service;

import com.unasp.taskmanagement.config.messages.Messages;
import com.unasp.taskmanagement.domain.authentication.api.v1.request.AuthenticationRequest;
import com.unasp.taskmanagement.domain.authentication.api.v1.request.ResetPasswordRequest;
import com.unasp.taskmanagement.domain.authentication.api.v1.request.SendTokenRequest;
import com.unasp.taskmanagement.domain.authentication.api.v1.response.AuthenticationResponse;
import com.unasp.taskmanagement.domain.user.entity.User;
import org.springframework.security.authentication.AuthenticationManager;

public interface AuthenticationService {
  AuthenticationResponse login (AuthenticationRequest authenticationRequest, AuthenticationManager authenticationManager);
  User getAuthenticatedUser();
  Messages sendToken(SendTokenRequest sendTokenRequest);
  Messages newPassword(ResetPasswordRequest resetPasswordRequest);
}