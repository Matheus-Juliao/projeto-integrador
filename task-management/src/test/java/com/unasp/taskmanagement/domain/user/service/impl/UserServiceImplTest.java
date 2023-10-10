package com.unasp.taskmanagement.domain.user.service.impl;

import com.unasp.taskmanagement.config.component.MessageProperty;
import com.unasp.taskmanagement.config.messages.Messages;
import com.unasp.taskmanagement.domain.authentication.service.impl.AuthenticationServiceImpl;
import com.unasp.taskmanagement.domain.task.repository.TaskRepository;
import com.unasp.taskmanagement.domain.user.api.v1.request.UserChildRequest;
import com.unasp.taskmanagement.domain.user.api.v1.request.UserChildUpdateRequest;
import com.unasp.taskmanagement.domain.user.api.v1.request.UserSponsorRequest;
import com.unasp.taskmanagement.domain.user.api.v1.response.UserChildResponse;
import com.unasp.taskmanagement.domain.user.entity.User;
import com.unasp.taskmanagement.domain.user.enums.UserRole;
import com.unasp.taskmanagement.domain.user.repository.UserRepository;
import com.unasp.taskmanagement.exception.BusinessException;
import com.unasp.taskmanagement.exception.NotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCrypt;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
  @Mock
  private UserRepository userRepository;
  @Mock
  private TaskRepository taskRepository;
  @Mock
  private MessageProperty messageProperty;
  @Mock
  private AuthenticationServiceImpl authenticationService;

  @InjectMocks
  private UserServiceImpl userService;

  private String externalId;

  @BeforeEach
  void setup() {
    externalId = "104dacaf-cd40-46f7-bb24-a7c5ee64ae59";
  }

  @Test
  void mustCreateSponsor() throws Exception {
    when(userRepository.findByLogin(anyString())).thenReturn(Optional.empty());

    Messages messages = userService.createSponsor(getUserSponsorRequest());
    verify(userRepository, times(1)).save(any());
    assertEquals(messageProperty.getProperty("success.register", messageProperty.getProperty("user")), messages.getMessage());
  }

  @Test
  void mustThrowsAlreadyEmail_WhenCreateSponsor() throws Exception {
    when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(getUser()));

    Exception exception = assertThrows(BusinessException.class, () -> userService.createSponsor(getUserSponsorRequest()));
    assertEquals(messageProperty.getProperty("error.already.email", messageProperty.getProperty("user")), exception.getMessage());
  }

  @Test
  void mustCreateChild() throws Exception {
    when(userRepository.findByLogin(anyString())).thenReturn(Optional.empty());
    when(authenticationService.getAuthenticatedUser()).thenReturn(getUser());

    UserChildResponse userChildResponse = userService.createChild(getUserChildRequest());
    verify(userRepository, times(1)).save(any());
    assertEquals(userChildResponse.getNickname(), getUserChildResponse().getNickname());
  }

  @Test
  void mustThrowsAlreadyNickname_WhenCreateChild() {
    when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(getUser()));

    Exception exception = assertThrows(BusinessException.class, () -> userService.createChild(getUserChildRequest()));
    assertEquals(messageProperty.getProperty("error.already.nickname", messageProperty.getProperty("user")), exception.getMessage());
  }

  @Test
  void mustListAllChild() throws Exception {
    when(userRepository.findByUserCreator(anyString())).thenReturn(List.of(getUser()));
    when(taskRepository.totalTask(anyString())).thenReturn(0);

    List<UserChildResponse> list = userService.listAllChild(externalId);
    verify(taskRepository, times(1)).totalTask(anyString());
    assertEquals(list.size(), 1);
  }

  @Test
  void mustUpdateChild_WhenNotChangingNicknameAndPassword() throws Exception {
    when(userRepository.findByExternalId(anyString())).thenReturn(Optional.of(getUser()));

    UserChildResponse userChildResponse = userService.updateChild(externalId, getUserChildUpdateRequest());
    verify(userRepository, times(1)).save(any());
    assertEquals(userChildResponse.getNickname(), getUser().getLogin());
  }

  @Test
  void mustUpdateChild_WhenChangingNicknameAndPassword() throws Exception {
    User user = getUser();
    user.setLogin("newNickname");
    user.setPassword("newPassword");
    when(userRepository.findByExternalId(anyString())).thenReturn(Optional.of(user));

    UserChildUpdateRequest userChildUpdateRequest = getUserChildUpdateRequest();
    userChildUpdateRequest.setNickname("newNickname");
    userChildUpdateRequest.setPassword("newPassword");

    UserChildResponse userChildResponse = userService.updateChild(externalId, userChildUpdateRequest);
    verify(userRepository, times(1)).save(any());
    assertEquals(userChildResponse.getNickname(), user.getLogin());
  }

  @Test
  void mustThrowsAlreadyNickname_WhenUpdateChild() throws Exception {
    when(userRepository.findByExternalId(anyString())).thenReturn(Optional.of(getUser()));
    when(userRepository.findByLogin(anyString())).thenReturn(Optional.of(getUser()));
    UserChildUpdateRequest userChildUpdateRequest = getUserChildUpdateRequest();
    userChildUpdateRequest.setNickname("newNickname");

    Exception exception = assertThrows(BusinessException.class, () -> userService.updateChild(externalId, userChildUpdateRequest));
    assertEquals(messageProperty.getProperty("error.already.email", messageProperty.getProperty("user")), exception.getMessage());
  }

  @Test
  void mustDeleteChild() throws Exception {
    when(userRepository.findByExternalId(anyString())).thenReturn(Optional.of(getUser()));

    Messages messages = userService.deleteChild(externalId);
    verify(taskRepository, times(1)).deleteExternalIdUserChild(anyString());
    verify(userRepository, times(1)).delete(any());
    assertEquals(messageProperty.getProperty("success.delete", messageProperty.getProperty("user")), messages.getMessage());
  }

  @Test
  void mustThrowsUserNotFound_WhenDeleteChild() throws Exception {
    when(userRepository.findByExternalId(anyString())).thenReturn(Optional.empty());

    Exception exception = assertThrows(NotFoundException.class, () -> userService.deleteChild(externalId));
    assertEquals(messageProperty.getProperty("error.notFound", messageProperty.getProperty("user")), exception.getMessage());
  }

  @Test
  void mustListChild() throws Exception {
    when(userRepository.findByExternalId(anyString())).thenReturn(Optional.of(getUser()));

    UserChildResponse userChildResponse = userService.listChild(externalId);
    verify(taskRepository, times(1)).totalTask(anyString());
    assertEquals(userChildResponse.getName(), getUserChildResponse().getName());
  }

  @Test
  void mustThrowsUserNotFound_WhenDListChild() throws Exception {
    when(userRepository.findByExternalId(anyString())).thenReturn(Optional.empty());

    Exception exception = assertThrows(NotFoundException.class, () -> userService.listChild(externalId));
    assertEquals(messageProperty.getProperty("error.notFound", messageProperty.getProperty("user")), exception.getMessage());
  }

  private User getUser() {
    return User.builder()
        .externalId(externalId)
        .name("Name")
        .login("email@test.com")
        .token(BCrypt.hashpw("token", BCrypt.gensalt()))
        .expiryDateToken(java.sql.Timestamp.valueOf(LocalDateTime.now().plusMinutes(10)))
        .build();
  }

  private UserSponsorRequest getUserSponsorRequest() {
    return UserSponsorRequest.builder()
        .name("Name")
        .email("email@test.com")
        .password("password")
        .readTerms(true)
        .build();
  }

  private UserChildRequest getUserChildRequest() {
    return UserChildRequest.builder()
        .name("Name")
        .nickname("nickname")
        .password("password")
        .age(5)
        .build();
  }

  private UserChildResponse getUserChildResponse() {
    return UserChildResponse.builder()
        .externalId(externalId)
        .name("Name")
        .nickname("nickname")
        .age(5)
        .role(UserRole.CHILD)
        .numberTasks(0)
        .build();
  }

  private UserChildUpdateRequest getUserChildUpdateRequest() {
    return UserChildUpdateRequest.builder()
        .name("Name")
        .age(5)
        .build();
  }

}
