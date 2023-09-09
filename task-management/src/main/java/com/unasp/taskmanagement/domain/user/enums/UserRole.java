package com.unasp.taskmanagement.domain.user.enums;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;

@Getter
public enum UserRole {
  SPONSOR("SPONSOR"),
  CHILD("CHILD");

  private String role;

  UserRole(String role) {
    this.role = role;
  }

  public static List<UserRole> getAllRoles() {
    return Arrays.asList(UserRole.values());
  }
}