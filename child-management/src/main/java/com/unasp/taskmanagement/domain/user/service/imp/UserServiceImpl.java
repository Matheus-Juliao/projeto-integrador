package com.unasp.taskmanagement.domain.user.service.imp;

import com.unasp.taskmanagement.domain.user.api.v1.request.UserRequest;
import com.unasp.taskmanagement.domain.user.entity.User;
import com.unasp.taskmanagement.domain.user.repository.UserRepository;
import com.unasp.taskmanagement.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
  @Autowired
  UserRepository userRepository;

  @Transactional
  @Override
  public String create(UserRequest userRequest) {
    User user = userRequest.converter();
    userRepository.save(user);
    return "Usuário salvo com sucesso!";
  }
}
