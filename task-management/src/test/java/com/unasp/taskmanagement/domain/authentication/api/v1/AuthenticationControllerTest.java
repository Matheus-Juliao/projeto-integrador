package com.unasp.taskmanagement.domain.authentication.api.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unasp.taskmanagement.config.component.MessageProperty;
import com.unasp.taskmanagement.config.messages.Messages;
import com.unasp.taskmanagement.config.service.TokenService;
import com.unasp.taskmanagement.domain.authentication.api.v1.request.AuthenticationRequest;
import com.unasp.taskmanagement.domain.authentication.api.v1.request.SendTokenRequest;
import com.unasp.taskmanagement.domain.authentication.api.v1.response.AuthenticationResponse;
import com.unasp.taskmanagement.domain.authentication.service.impl.AuthenticationServiceImpl;
import com.unasp.taskmanagement.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = AuthenticationController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class AuthenticationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private AuthenticationServiceImpl authenticationService;

  @MockBean
  private MessageProperty messageProperty;

  @MockBean
  AuthenticationManager authenticationManager;

  @MockBean
  UserRepository userRepository;

  @MockBean
  private TokenService tokenService;

  private HttpHeaders httpHeaders;
  private final String urlBase = "/v1/auth";

  @BeforeEach
  void setup() {
    httpHeaders = new HttpHeaders();
    mapper = new ObjectMapper();
  }

  @Test
  void mustLogin() throws Exception {
    AuthenticationRequest authenticationRequest = new AuthenticationRequest();
    authenticationRequest.setLogin("Login");
    authenticationRequest.setPassword("Password");
    String token = "token";
    AuthenticationResponse authenticationResponse = AuthenticationResponse.builder().build().converter(token);

    when(authenticationService.login(any(), any())).thenReturn(authenticationResponse);

    mockMvc.perform(MockMvcRequestBuilders
        .post(urlBase + "/login")
        .headers(httpHeaders)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(authenticationRequest)))
        .andExpect(status().isOk());
  }

  @Test
  void mustSendToken() throws Exception {
    SendTokenRequest sendTokenRequest = new SendTokenRequest();
    sendTokenRequest.setEmail("email@test.com");

    when(authenticationService.sendToken(sendTokenRequest)).thenReturn(Messages.builder().build().converter("Token successfully sent", HttpStatus.OK.value()));
//    when(authenticationService.sendToken(sendTokenRequest)).thenReturn(Messages.builder().build().converter(messageProperty.getProperty("success.sendToken"), HttpStatus.OK.value()));

    mockMvc.perform(MockMvcRequestBuilders
            .post(urlBase + "/send-token")
            .headers(httpHeaders)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(sendTokenRequest)))
            .andExpect(status().isOk());
  }

}