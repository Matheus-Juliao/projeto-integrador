package com.unasp.taskmanagement.domain.user.service;

import com.unasp.taskmanagement.domain.user.api.v1.request.UserRequest;

public interface UserService {
  String create(UserRequest userRequest);
}
