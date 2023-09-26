package com.unasp.taskmanagement.domain.authentication.service.impl;

import com.unasp.taskmanagement.config.component.MessageProperty;
import com.unasp.taskmanagement.config.messages.Messages;
import com.unasp.taskmanagement.config.service.TokenService;
import com.unasp.taskmanagement.domain.authentication.api.v1.request.AuthenticationRequest;
import com.unasp.taskmanagement.domain.authentication.api.v1.request.SendTokenRequest;
import com.unasp.taskmanagement.domain.authentication.api.v1.response.AuthenticationResponse;
import com.unasp.taskmanagement.domain.authentication.service.AuthenticationService;
import com.unasp.taskmanagement.domain.user.entity.User;
import com.unasp.taskmanagement.domain.user.repository.UserRepository;
import com.unasp.taskmanagement.exception.BusinessException;
import com.unasp.taskmanagement.exception.NotFoundException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@SuppressWarnings("unused")
public class AuthenticationServiceImpl implements AuthenticationService, UserDetailsService {
  @Autowired
  UserRepository userRepository;
  @Autowired
  private TokenService tokenService;
  @Autowired
  MessageProperty messageProperty;
  @Autowired
  private JavaMailSender emailSender;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Optional<User> userOptional = userRepository.findByLogin(username);
    if(userOptional.isEmpty()) {
      throw new NotFoundException(messageProperty.getProperty("error.notFound", messageProperty.getProperty("user")));
    }

    return userOptional.get();
  }

  @Override
  public AuthenticationResponse login(AuthenticationRequest authenticationRequest, AuthenticationManager authenticationManager) {
    var usernamePassword = new UsernamePasswordAuthenticationToken(authenticationRequest.getLogin(), authenticationRequest.getPassword());
    var auth = authenticationManager.authenticate(usernamePassword);
    var token = tokenService.generateToken((User) auth.getPrincipal());

    return AuthenticationResponse.builder().build().converter(token);
  }

  @Override
  public User getAuthenticatedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    return (User) authentication.getPrincipal();
  }

  @Override
  public Messages sendToken(SendTokenRequest sendTokenRequest) {
    Optional<User> userOptional = userRepository.findByLogin(sendTokenRequest.getEmail());
    if(userOptional.isEmpty()) {
      throw new NotFoundException(messageProperty.getProperty("error.notFound", messageProperty.getProperty("user")));
    }
    User user = userOptional.get();

    try {
      SimpleMailMessage message = new SimpleMailMessage();
//      message.setFrom("matheusjosejuliao@gmail.com");
      message.setTo(user.getLogin());
      message.setSubject("Email Confirmation");
      message.setText("Access token");
      emailSender.send(message);

    } catch (MailException e) {
      System.out.println(e);
      throw new BusinessException(messageProperty.getProperty("error.token"));

    }

    return Messages.builder().build().converter(messageProperty.getProperty("success.sendToken"), HttpStatus.OK.value());
  }

}