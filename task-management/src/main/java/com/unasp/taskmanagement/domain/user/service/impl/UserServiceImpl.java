package com.unasp.taskmanagement.domain.user.service.impl;

import com.unasp.taskmanagement.config.messages.Messages;
import com.unasp.taskmanagement.config.component.MessageProperty;
import com.unasp.taskmanagement.domain.authentication.service.AuthenticationService;
import com.unasp.taskmanagement.domain.user.api.v1.request.UserChildRequest;
import com.unasp.taskmanagement.domain.user.api.v1.request.UserSponsorRequest;
import com.unasp.taskmanagement.domain.user.api.v1.response.UserChildResponse;
import com.unasp.taskmanagement.domain.user.entity.User;
import com.unasp.taskmanagement.domain.user.repository.UserRepository;
import com.unasp.taskmanagement.domain.user.service.UserService;
import com.unasp.taskmanagement.exception.BusinessException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
  @Autowired
  UserRepository userRepository;
  @Autowired
  MessageProperty messageProperty;
  @Autowired
  AuthenticationService authenticationService;


  @Transactional
  @Override
  public Messages createSponsor(UserSponsorRequest userSponsorRequest) {
    if(userRepository.findByLogin(userSponsorRequest.getEmail()).isPresent()) {
      throw new BusinessException(messageProperty.getProperty("error.already.account", messageProperty.getProperty("user")));
    }
    User user = userSponsorRequest.converter();
    userRepository.save(user);

    return Messages.builder().build().converter(messageProperty.getProperty("success.register", messageProperty.getProperty("user")), HttpStatus.CREATED.value());
  }

  @Transactional
  @Override
  public UserChildResponse createChild(UserChildRequest userChildRequest) {
    if(userRepository.findByLogin(userChildRequest.getNickname()).isPresent()) {
      throw new BusinessException(messageProperty.getProperty("error.already.account", messageProperty.getProperty("user")));
    }
    User user = userChildRequest.converter(authenticationService.getAuthenticatedUser().getExternalId());
    userRepository.save(user);

    return UserChildResponse.builder().build().converter(user);
  }

  @Override
  public List<UserChildResponse> listChild(String externalId) {
     return userRepository.findByUserCreator(externalId)
         .stream().map(user -> UserChildResponse.builder()
             .externalId(user.getExternalId())
             .userCreator(user.getUserCreator())
             .name(user.getName())
             .role(user.getRole())
             .nickname(user.getLogin())
             .age(user.getAge())
             .build())
         .collect(Collectors.toList());
  }
}