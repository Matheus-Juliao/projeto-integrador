package com.unasp.taskmanagement.domain.authentication.service.impl;

import com.unasp.taskmanagement.config.component.MessageProperty;
import com.unasp.taskmanagement.config.messages.Messages;
import com.unasp.taskmanagement.config.service.TokenService;
import com.unasp.taskmanagement.domain.authentication.api.v1.request.AuthenticationRequest;
import com.unasp.taskmanagement.domain.authentication.api.v1.request.ResetPasswordRequest;
import com.unasp.taskmanagement.domain.authentication.api.v1.request.SendTokenRequest;
import com.unasp.taskmanagement.domain.authentication.api.v1.response.AuthenticationResponse;
import com.unasp.taskmanagement.domain.authentication.service.AuthenticationService;
import com.unasp.taskmanagement.domain.user.entity.User;
import com.unasp.taskmanagement.domain.user.repository.UserRepository;
import com.unasp.taskmanagement.exception.BusinessException;
import com.unasp.taskmanagement.exception.NotFoundException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import jakarta.transaction.Transactional;
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
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService, UserDetailsService {
  private static final int TOKEN_LENGTH = 5;
  private static final int EXPIRATION_MINUTES = 10;
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
  @Transactional
  public Messages sendToken(SendTokenRequest sendTokenRequest) {
    Optional<User> userOptional = userRepository.findByLogin(sendTokenRequest.getEmail());
    if(userOptional.isEmpty()) {
      throw new NotFoundException(messageProperty.getProperty("error.notFound", messageProperty.getProperty("user")));
    }
    User user = userOptional.get();
    String token = generateToken();
    Date expirationDate = calculateExpirationDate();

    user.setToken(new BCryptPasswordEncoder().encode(token));
    user.setExpiryDateToken(expirationDate);
    userRepository.save(user);

    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(user.getLogin());
      message.setSubject("Email Confirmation");
      message.setText("Your confirmation token for changing your password is " + token);
      emailSender.send(message);

    } catch (MailException e) {
        throw new BusinessException(messageProperty.getProperty("error.token"));
    }

    return Messages.builder().build().converter(messageProperty.getProperty("success.sendToken"), HttpStatus.OK.value());
  }

  @Override
  public Messages newPassword(ResetPasswordRequest resetPasswordRequest) {
    Optional<User> userOptional = userRepository.findByLogin(resetPasswordRequest.getEmail());
    if(userOptional.isEmpty()) {
      throw new NotFoundException(messageProperty.getProperty("error.notFound", messageProperty.getProperty("user")));
    }
    if(!BCrypt.checkpw(resetPasswordRequest.getToken(), userOptional.get().getToken())) {
      throw new BusinessException(messageProperty.getProperty("error.invalidToken"));
    }
    Date now = new Date();
    if(userOptional.get().getExpiryDateToken().before(now)) {
      throw new BusinessException(messageProperty.getProperty("error.expirationDate"));
    }

    userOptional.get().setPassword(new BCryptPasswordEncoder().encode(resetPasswordRequest.getNewPassword()));
    userOptional.get().setUpdatedDate(LocalDateTime.now(ZoneId.of("UTC")));
    userRepository.save(userOptional.get());

    return Messages.builder().build().converter(messageProperty.getProperty("success.newPassword"), HttpStatus.OK.value());
  }

  private String generateToken() {
    SecureRandom random = new SecureRandom();
    byte[] tokenBytes = new byte[TOKEN_LENGTH];
    random.nextBytes(tokenBytes);
    String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    return token.substring(0, TOKEN_LENGTH);
  }

  private Date calculateExpirationDate() {
    long currentTimeMillis = System.currentTimeMillis();
    long expirationMillis = EXPIRATION_MINUTES * 60 * 1000;
    return new Date(currentTimeMillis + expirationMillis);
  }

}