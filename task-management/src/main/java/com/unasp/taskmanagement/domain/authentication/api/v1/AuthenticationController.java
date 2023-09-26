package com.unasp.taskmanagement.domain.authentication.api.v1;

import com.unasp.taskmanagement.config.messages.Messages;
import com.unasp.taskmanagement.domain.authentication.api.v1.request.AuthenticationRequest;
import com.unasp.taskmanagement.domain.authentication.api.v1.request.SendTokenRequest;
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
@SuppressWarnings("unused")
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
  public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
    log.info("Start authentication {}", authenticationRequest.getLogin());
    AuthenticationResponse authenticationResponse = authenticationService.login(authenticationRequest, authenticationManager);
    log.info("Finalize authentication {}", authenticationRequest.getLogin());

    return ResponseEntity.status(HttpStatus.OK).body(authenticationResponse);
  }

  @PostMapping("/send-token")
  @Operation(summary = "Send token", description = "API for send token by email on the platform")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Ok", content =  { @Content(mediaType = "application/json", schema = @Schema(implementation = Messages.class)) }),
          @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
  })
  public ResponseEntity<Messages> sendToken(@RequestBody @Valid SendTokenRequest sendTokenRequest) {
    log.info("Start send token {}", sendTokenRequest.getEmail());
    Messages messages = authenticationService.sendToken(sendTokenRequest);
    log.info("Finalize send token {}", sendTokenRequest.getEmail());

    return ResponseEntity.status(HttpStatus.OK).body(messages);
  }

//  @PostMapping("/new-password")
//  @Operation(summary = "Redefine password", description = "API for a user reset login password on the platform")
//  @ApiResponses(value = {
//          @ApiResponse(responseCode = "200", description = "Ok", content =  { @Content(mediaType = "application/json", schema = @Schema(implementation = Messages.class)) }),
//          @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
//  })
//  public ResponseEntity<Messages> newPassword(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
//    log.info("Start password reset {}", authenticationRequest.getLogin());
//    AuthenticationResponse authenticationResponse = authenticationService.login(authenticationRequest, authenticationManager);
//    log.info("Finalize password reset {}", authenticationRequest.getLogin());
//
//    return ResponseEntity.status(HttpStatus.OK).body(authenticationResponse);
//  }
}