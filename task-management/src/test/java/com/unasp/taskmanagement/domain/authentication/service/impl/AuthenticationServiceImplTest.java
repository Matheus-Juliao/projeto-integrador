package com.unasp.taskmanagement.domain.authentication.service.impl;

import com.unasp.taskmanagement.config.component.MessageProperty;
import com.unasp.taskmanagement.config.messages.Messages;
import com.unasp.taskmanagement.config.service.TokenService;
import com.unasp.taskmanagement.domain.authentication.api.v1.request.AuthenticationRequest;
import com.unasp.taskmanagement.domain.authentication.api.v1.request.ResetPasswordRequest;
import com.unasp.taskmanagement.domain.authentication.api.v1.request.SendTokenRequest;
import com.unasp.taskmanagement.domain.authentication.api.v1.response.AuthenticationResponse;
import com.unasp.taskmanagement.domain.user.entity.User;
import com.unasp.taskmanagement.domain.user.repository.UserRepository;
import com.unasp.taskmanagement.exception.BusinessException;
import com.unasp.taskmanagement.exception.NotFoundException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {

  @Mock
  AuthenticationManager authenticationManager;
  @Mock
  private UserRepository userRepository;
  @Mock
  private MessageProperty messageProperty;
  @Mock
  private TokenService tokenService;
  @Mock
  private JavaMailSender emailSender;

  @InjectMocks
  private AuthenticationServiceImpl authenticationService;

  @Test
  void mustLoadUserByUsername () throws Exception {
    when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(getUser()));

    authenticationService.loadUserByUsername(anyString());

    verify(userRepository, times(1)).findByLogin(anyString());
  }

  @Test
  void mustThrowsUserNotFound_WhenLoadUserByUsername() throws Exception {
    when(userRepository.findByLogin(anyString())).thenReturn(Optional.empty());

    Exception exception = assertThrows(NotFoundException.class, () -> authenticationService.loadUserByUsername(anyString()));
    assertEquals(messageProperty.getProperty("error.notFound", messageProperty.getProperty("user")), exception.getMessage());
  }

  @Test
  void mustLogin() throws Exception {
    AuthenticationRequest authenticationRequest = new AuthenticationRequest();
    authenticationRequest.setLogin("username");
    authenticationRequest.setPassword("password");
    Authentication authentication = new UsernamePasswordAuthenticationToken(getUser(), authenticationRequest.getPassword());
    String token = "token";

    when(authenticationManager.authenticate(any())).thenReturn(authentication);

    when(tokenService.generateToken(any())).thenReturn(token);

    AuthenticationResponse authenticationResponse = authenticationService.login(authenticationRequest, authenticationManager);
    assertEquals(authenticationResponse.getToken(), token);
  }

  @Test
  void mustGetAuthenticatedUser() throws Exception {
    User user = getUser();
    Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    User authenticationUser = authenticationService.getAuthenticatedUser();
    assertEquals(user, authenticationUser);
  }

  @Test
  void mustSendToken() throws Exception {
    when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(getUser()));

    Messages messages = authenticationService.sendToken(getSendTokenRequest());

    assertEquals(messageProperty.getProperty("success.sendToken"), messages.getMessage());
    assertEquals(HttpStatus.OK.value(), messages.getCode());
  }

  @Test
  void mustThrowsUserNotFound_WhenSendToken() throws Exception {
    when(userRepository.findByLogin(anyString())).thenReturn(Optional.empty());

    Exception exception = assertThrows(NotFoundException.class, () -> authenticationService.sendToken(getSendTokenRequest()));
    assertEquals(messageProperty.getProperty("error.notFound", messageProperty.getProperty("user")), exception.getMessage());
  }

  @Test
  void mustThrowsFailed_WhenSendToken() throws Exception {
    when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(getUser()));

    doThrow(new MailException(messageProperty.getProperty("error.token")) {}).when(emailSender).send(any(
        SimpleMailMessage.class));

    Exception exception = assertThrows(BusinessException.class, () -> authenticationService.sendToken(getSendTokenRequest()));
    assertEquals(messageProperty.getProperty("error.token"), exception.getMessage());
  }

  @Test
  void mustNewPassword() throws Exception {
    when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(getUser()));

    Messages messages = authenticationService.newPassword(getResetPasswordRequest());

    assertEquals(messageProperty.getProperty("success.newPassword"), messages.getMessage());
    assertEquals(HttpStatus.OK.value(), messages.getCode());
  }

  private User getUser() {
    return User.builder()
        .externalId("63f37d4a-a4df-4beb-be6c-2f84695987c8")
        .name("Test")
        .login("email@test.com")
        .token(BCrypt.hashpw("token", BCrypt.gensalt()))
//        .expiryDateToken()
        .build();
  }

  private SendTokenRequest getSendTokenRequest() {
    SendTokenRequest sendTokenRequest = new SendTokenRequest();
    sendTokenRequest.setEmail("email@test.com");

    return sendTokenRequest;
  }

  private ResetPasswordRequest getResetPasswordRequest() {
    ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest();
    resetPasswordRequest.setEmail("email@test.com");
    resetPasswordRequest.setToken("token");
    resetPasswordRequest.setNewPassword("new-password");

    return resetPasswordRequest;
  }

}