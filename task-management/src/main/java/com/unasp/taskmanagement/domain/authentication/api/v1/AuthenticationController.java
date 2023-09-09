package com.unasp.taskmanagement.domain.authentication.api.v1;

import com.unasp.taskmanagement.domain.authentication.api.v1.request.RequestAuthentication;
import com.unasp.taskmanagement.domain.authentication.api.v1.response.AuthenticationResponse;
import com.unasp.taskmanagement.domain.authentication.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@Slf4j
public class AuthenticationController {
  @Autowired
  AuthenticationService authenticationService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @PostMapping("/login")
  @Operation(summary = "Login the user into the application", description = "API for a user to authenticate on the platform")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Ok", content =  { @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationResponse.class)) }),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
  })
  public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid RequestAuthentication requestAuthentication) {
    log.info("Start authentication {}", requestAuthentication.getLogin());
    AuthenticationResponse authenticationResponse = authenticationService.login(requestAuthentication, authenticationManager);
    log.info("Finalize authentication {}", requestAuthentication.getLogin());

    return ResponseEntity.status(HttpStatus.OK).body(authenticationResponse);
  }

}