package com.unasp.taskmanagement.domain.authentication.service.impl;

import com.unasp.taskmanagement.config.component.MessageProperty;
import com.unasp.taskmanagement.config.service.TokenService;
import com.unasp.taskmanagement.domain.authentication.api.v1.request.AuthenticationRequest;
import com.unasp.taskmanagement.domain.authentication.api.v1.response.AuthenticationResponse;
import com.unasp.taskmanagement.domain.user.entity.User;
import com.unasp.taskmanagement.domain.user.repository.UserRepository;
import com.unasp.taskmanagement.exception.NotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private MessageProperty messageProperty;
  @Mock
  private TokenService tokenService;
  @Mock
  AuthenticationManager authenticationManager;

  @InjectMocks
  private AuthenticationServiceImpl authenticationService;

  @Test
  void mustLoadUserByUsername () throws Exception {
    User user = new User();
    when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(user));

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
    Authentication authentication = new UsernamePasswordAuthenticationToken(new User(), authenticationRequest.getPassword());
    String token = "token";

    when(authenticationManager.authenticate(any())).thenReturn(authentication);

    when(tokenService.generateToken(any())).thenReturn(token);

    AuthenticationResponse authenticationResponse = authenticationService.login(authenticationRequest, authenticationManager);

    assertEquals(authenticationResponse.getToken(), token);
  }

  @Test
  void mustGetAuthenticatedUser() throws Exception {
    User user =new User();
    Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    User authenticationUser = authenticationService.getAuthenticatedUser();

    assertEquals(user, authenticationUser);
  }

}