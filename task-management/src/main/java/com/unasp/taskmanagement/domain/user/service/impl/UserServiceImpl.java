package com.unasp.taskmanagement.domain.user.service.impl;

import com.unasp.taskmanagement.config.messages.Messages;
import com.unasp.taskmanagement.config.component.MessageProperty;
import com.unasp.taskmanagement.domain.authentication.service.AuthenticationService;
import com.unasp.taskmanagement.domain.task.repository.TaskRepository;
import com.unasp.taskmanagement.domain.user.api.v1.request.UserChildRequest;
import com.unasp.taskmanagement.domain.user.api.v1.request.UserChildUpdateRequest;
import com.unasp.taskmanagement.domain.user.api.v1.request.UserSponsorRequest;
import com.unasp.taskmanagement.domain.user.api.v1.response.UserChildResponse;
import com.unasp.taskmanagement.domain.user.entity.User;
import com.unasp.taskmanagement.domain.user.repository.UserRepository;
import com.unasp.taskmanagement.domain.user.service.UserService;
import com.unasp.taskmanagement.exception.BusinessException;
import com.unasp.taskmanagement.exception.NotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("unused")
public class UserServiceImpl implements UserService {
  @Autowired
  UserRepository userRepository;
  @Autowired
  MessageProperty messageProperty;
  @Autowired
  AuthenticationService authenticationService;
  @Autowired
  TaskRepository taskRepository;

  @Transactional
  @Override
  public Messages createSponsor(UserSponsorRequest userSponsorRequest) {
    if(userRepository.findByLogin(userSponsorRequest.getEmail()).isPresent()) {
      throw new BusinessException(messageProperty.getProperty("error.already.email", messageProperty.getProperty("user")));
    }
    User user = userSponsorRequest.converter();
    userRepository.save(user);

    return Messages.builder().build().converter(messageProperty.getProperty("success.register", messageProperty.getProperty("user")), HttpStatus.CREATED.value());
  }

  @Transactional
  @Override
  public UserChildResponse createChild(UserChildRequest userChildRequest) {
    if(userRepository.findByLogin(userChildRequest.getNickname()).isPresent()) {
      throw new BusinessException(messageProperty.getProperty("error.already.nickname", messageProperty.getProperty("user")));
    }
    User user = userChildRequest.converter(authenticationService.getAuthenticatedUser().getExternalId());
    userRepository.save(user);

    return UserChildResponse.builder().build().converter(user, 0);
  }

  @Override
  public List<UserChildResponse> listChild(String externalId) {
     return userRepository.findByUserCreator(externalId)
         .stream().map(user -> UserChildResponse.builder()
             .externalId(user.getExternalId())
             .name(user.getName())
             .role(user.getRole())
             .nickname(user.getLogin())
             .age(user.getAge())
             .numberTasks(taskRepository.totalTask(user.getExternalId()))
             .build())
         .collect(Collectors.toList());
  }

  @Transactional
  @Override
  public UserChildResponse updateChild(String externalId, UserChildUpdateRequest userChildUpdateRequest) {
    Optional<User> userOptional = userRepository.findByExternalId(externalId);
    if(userOptional.isEmpty()) {
      throw new NotFoundException(messageProperty.getProperty("error.notFound", messageProperty.getProperty("user")));
    }

    if(userChildUpdateRequest.getNickname() != null && !userOptional.get().getLogin().equals(userChildUpdateRequest.getNickname())) {
      if(userRepository.findByLogin(userChildUpdateRequest.getNickname()).isPresent()) {
        throw new BusinessException(messageProperty.getProperty("error.already.nickname", messageProperty.getProperty("user")));
      }
    }
    User user = userOptional.get();
    userChildUpdateRequest.setPassword(userChildUpdateRequest.getPassword() == null ? user.getPassword() : new BCryptPasswordEncoder().encode(userChildUpdateRequest.getPassword()));
    BeanUtils.copyProperties(userChildUpdateRequest, user);
    user.setUpdatedDate(LocalDateTime.now(ZoneId.of("UTC")));
    userRepository.save(user);

    return UserChildResponse.builder().build().converter(user, taskRepository.totalTask(user.getExternalId()));
  }

  @Transactional
  @Override
  public Messages deleteChild(String externalId) {
    Optional<User> userOptional = userRepository.findByExternalId(externalId);
    if(userOptional.isEmpty()) {
      throw new NotFoundException(messageProperty.getProperty("error.notFound", messageProperty.getProperty("user")));
    }
    taskRepository.deleteExternalIdUserChild(externalId);
    userRepository.delete(userOptional.get());

    return Messages.builder().build().converter(messageProperty.getProperty("success.delete", messageProperty.getProperty("user")), HttpStatus.OK.value());
  }
}