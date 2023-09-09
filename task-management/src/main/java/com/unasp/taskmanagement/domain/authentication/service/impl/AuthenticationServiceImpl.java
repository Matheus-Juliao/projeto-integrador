package com.unasp.taskmanagement.domain.authentication.service.impl;

import com.unasp.taskmanagement.config.service.TokenService;
import com.unasp.taskmanagement.domain.authentication.api.v1.request.RequestAuthentication;
import com.unasp.taskmanagement.domain.authentication.api.v1.response.AuthenticationResponse;
import com.unasp.taskmanagement.domain.authentication.service.AuthenticationService;
import com.unasp.taskmanagement.domain.user.entity.User;
import com.unasp.taskmanagement.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService, UserDetailsService {
  @Autowired
  UserRepository userRepository;

  @Autowired
  private TokenService tokenService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByLogin(username).get();
  }

  @Override
  public AuthenticationResponse login(RequestAuthentication requestAuthentication, AuthenticationManager authenticationManager) {
    var usernamePassword = new UsernamePasswordAuthenticationToken(requestAuthentication.getLogin(), requestAuthentication.getPassword());
    var auth = authenticationManager.authenticate(usernamePassword);
    var token = tokenService.generateToken((User) auth.getPrincipal());

    return AuthenticationResponse.builder().build().converter(token);
  }
}
