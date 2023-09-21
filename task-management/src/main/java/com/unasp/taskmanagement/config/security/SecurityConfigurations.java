package com.unasp.taskmanagement.config.security;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@SuppressWarnings("unused")
public class SecurityConfigurations {
    @Autowired
    SecurityFilter securityFilter;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws  Exception {
    return httpSecurity
//          .cors().configurationSource(corsConfigurationSource).and()
          .cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource))
          .csrf(AbstractHttpConfigurer::disable)
          .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/**.html", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/v1/user/sponsor/new-user").permitAll()
            .requestMatchers(HttpMethod.POST, "/v1/auth/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/v1/user/child/new-user").hasRole("SPONSOR")
            .requestMatchers(HttpMethod.PUT, "/v1/user/update-child/{externalId}").hasRole("SPONSOR")
            .requestMatchers(HttpMethod.DELETE, "/v1/user/delete-child/{externalId}").hasRole("SPONSOR")
            .requestMatchers(HttpMethod.POST, "/v1/task").hasRole("SPONSOR")
            .requestMatchers(HttpMethod.PUT, "/v1/task/{externalId}").hasRole("SPONSOR")
            .requestMatchers(HttpMethod.DELETE, "/v1/task/{externalId}").hasRole("SPONSOR")
              .requestMatchers(HttpMethod.POST, "/v1/task/new-cicle").hasRole("SPONSOR")
            .anyRequest().authenticated()
          )
          .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
          .build();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
      return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("*"));
    configuration.setAllowedMethods(List.of("*"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }
}