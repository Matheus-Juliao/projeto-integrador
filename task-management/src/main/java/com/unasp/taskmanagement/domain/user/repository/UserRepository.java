package com.unasp.taskmanagement.domain.user.repository;

import com.unasp.taskmanagement.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByLogin(String login);
}