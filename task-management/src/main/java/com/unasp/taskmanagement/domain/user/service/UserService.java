package com.unasp.taskmanagement.domain.user.service;

import com.unasp.taskmanagement.config.messages.Messages;
import com.unasp.taskmanagement.domain.user.api.v1.request.UserChildRequest;
import com.unasp.taskmanagement.domain.user.api.v1.request.UserChildUpdateRequest;
import com.unasp.taskmanagement.domain.user.api.v1.request.UserSponsorRequest;
import com.unasp.taskmanagement.domain.user.api.v1.response.UserChildResponse;
import java.util.List;

public interface UserService {
  Messages createSponsor(UserSponsorRequest userSponsorRequest);
  UserChildResponse createChild(UserChildRequest userChildRequest);
  List<UserChildResponse> listChild(String externalId);
  UserChildResponse updateChild(String externalId, UserChildUpdateRequest userChildUpdateRequest);
  Messages deleteChild(String externalId);
}