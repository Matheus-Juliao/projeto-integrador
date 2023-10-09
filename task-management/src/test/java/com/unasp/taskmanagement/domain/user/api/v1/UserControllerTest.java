package com.unasp.taskmanagement.domain.user.api.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unasp.taskmanagement.config.component.MessageProperty;
import com.unasp.taskmanagement.config.messages.Messages;
import com.unasp.taskmanagement.config.service.TokenService;
import com.unasp.taskmanagement.domain.authentication.service.impl.AuthenticationServiceImpl;
import com.unasp.taskmanagement.domain.user.api.v1.request.UserChildRequest;
import com.unasp.taskmanagement.domain.user.api.v1.request.UserSponsorRequest;
import com.unasp.taskmanagement.domain.user.api.v1.response.UserChildResponse;
import com.unasp.taskmanagement.domain.user.entity.User;
import com.unasp.taskmanagement.domain.user.enums.UserRole;
import com.unasp.taskmanagement.domain.user.repository.UserRepository;
import com.unasp.taskmanagement.domain.user.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper mapper;

  @MockBean
  UserService userService;
  @MockBean
  private MessageProperty messageProperty;
  @MockBean
  private TokenService tokenService;
  @MockBean
  UserRepository userRepository;

  @MockBean
  private AuthenticationServiceImpl authenticationService;

  private HttpHeaders httpHeaders;
  private final String urlBase = "/v1/user";
  private String externalId;

  @BeforeEach
  void setup() {
    externalId = "104dacaf-cd40-46f7-bb24-a7c5ee64ae59";
    mapper = new ObjectMapper();
    httpHeaders = new HttpHeaders();
  }

  @Test
  void mustCreateSponsor() throws Exception {
    when(userService.createSponsor(any())).thenReturn(getMessage());

    mockMvc.perform(MockMvcRequestBuilders
        .post(urlBase + "/sponsor/new-user")
        .headers(httpHeaders)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(getUserSponsorRequest())))
        .andExpect(status().isCreated());
  }

  @Test
  void mustCreateChild() throws Exception {
    when(userService.createChild(any())).thenReturn(getUserChildResponse());

    mockMvc.perform(MockMvcRequestBuilders
        .post(urlBase + "/child/new-user")
        .headers(httpHeaders)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(getUserChildRequest())))
        .andExpect(status().isCreated());
  }

  @Test
  void mustListAllChild() throws Exception {
    setUserChild();
    setUserAuthentication();
    when(authenticationService.getAuthenticatedUser()).thenReturn(getUser());
    when(userService.listAllChild(any())).thenReturn(List.of(getUserChildResponse()));

    mockMvc.perform(MockMvcRequestBuilders
        .get(urlBase + "/list-child")
        .headers(httpHeaders)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }
  
  @Test
  void mustUpdateChild() throws Exception {
    when(userService.updateChild(anyString(),any())).thenReturn(getUserChildResponse());

    mockMvc.perform(MockMvcRequestBuilders
        .put(urlBase + "/update-child/{externalId}", externalId)
        .headers(httpHeaders)
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(getUserChildRequest())))
        .andExpect(status().isOk());
  }

  @Test
  void mustDeleteChild() throws Exception {
    when(userService.deleteChild(anyString())).thenReturn(getMessage());

    mockMvc.perform(MockMvcRequestBuilders
        .delete(urlBase + "/delete-child/{externalId}", externalId)
        .headers(httpHeaders)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void mustListChild() throws Exception {
    when(userService.listChild(anyString())).thenReturn(getUserChildResponse());

    mockMvc.perform(MockMvcRequestBuilders
        .get(urlBase + "/list-child/{externalId}", externalId)
        .headers(httpHeaders)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  private Messages getMessage() {
    return Messages.builder().build();
  }

  private UserSponsorRequest getUserSponsorRequest() {
    return UserSponsorRequest.builder()
        .name("Test")
        .email("email@test.com")
        .password("password")
        .readTerms(true)
        .build();
  }

  private UserChildResponse getUserChildResponse() {
    return UserChildResponse.builder()
        .externalId(externalId)
        .name("Test")
        .nickname("test")
        .age(5)
        .role(UserRole.CHILD)
        .numberTasks(0)
        .build();
  }

  private UserChildRequest getUserChildRequest() {
    return UserChildRequest.builder()
        .name("Test")
        .nickname("test")
        .password("password")
        .age(5)
        .build();
  }

  private User getUser() {
    return User.builder()
        .externalId(externalId)
        .name("Test")
        .login("email@test.com")
        .token(BCrypt.hashpw("token", BCrypt.gensalt()))
        .expiryDateToken(java.sql.Timestamp.valueOf(LocalDateTime.now().plusMinutes(10)))
        .build();
  }

  private void setUserChild() {
    when(userRepository.findByExternalId(anyString())).thenReturn(Optional.of(getUser()));
  }

  private void setUserAuthentication() {
    Authentication authentication = new UsernamePasswordAuthenticationToken(getUser(), null);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }
}
