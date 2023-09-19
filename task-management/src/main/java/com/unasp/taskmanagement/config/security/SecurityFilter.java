package com.unasp.taskmanagement.config.security;

import com.unasp.taskmanagement.config.component.MessageProperty;
import com.unasp.taskmanagement.config.service.TokenService;
import com.unasp.taskmanagement.domain.user.entity.User;
import com.unasp.taskmanagement.domain.user.repository.UserRepository;
import com.unasp.taskmanagement.exception.NotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class SecurityFilter extends OncePerRequestFilter {
  @Autowired
  TokenService tokenService;
  @Autowired
  UserRepository userRepository;

  @Autowired
  MessageProperty messageProperty;

  @Override
  protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
    var token = recoverToken(request);
    if(token != null) {
      var login = tokenService.validateToken(token);
      Optional<User> userOptional = userRepository.findByLogin(login);
      if(userOptional.isEmpty()) {
        throw new NotFoundException(messageProperty.getProperty("error.notFound", messageProperty.getProperty("user")));
      }
      UserDetails user = userOptional.get();

      var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    filterChain.doFilter(request, response);
  }

  private String recoverToken(HttpServletRequest request) {
    var authHeader = request.getHeader("Authorization");

    if(authHeader == null) return null;

    return authHeader.replace("Bearer ", "");
  }
}