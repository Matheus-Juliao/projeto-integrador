package com.unasp.taskmanagement.domain.user.api.v1;

import com.unasp.taskmanagement.config.messages.Messages;
import com.unasp.taskmanagement.domain.user.api.v1.request.UserSponsorRequest;
import com.unasp.taskmanagement.domain.user.service.UserService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user")
@Slf4j
public class UserController {
  @Autowired
  private UserService userService;
  @PostMapping("/sponsor/new-user")
  @Operation(summary = "Register sponsor", description = "Api for register a new sponsor on the platform")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created", content =  { @Content(mediaType = "application/json", schema = @Schema(implementation = Messages.class)) }),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(hidden = true)))
  })
  public ResponseEntity<Messages> createSponsor (@RequestBody @Valid UserSponsorRequest userSponsorRequest) {
    log.info("Start user registration {}", userSponsorRequest.getEmail());
    Messages user = userService.createSponsor(userSponsorRequest);
    log.info("Finalize user registration {}", userSponsorRequest.getEmail());

    return  ResponseEntity.status(HttpStatus.CREATED).body(user);
  }

//  @PostMapping("/child/new-user")
//  @Operation(summary = "Register child", description = "Api for register an new child on the platform")
//  @ApiResponses(value = {
//      @ApiResponse(responseCode = "201", description = "Created", content =  { @Content(mediaType = "application/json", schema = @Schema(implementation = UserChildResponse.class)) }),
//      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(hidden = true)))
//  })
//  public ResponseEntity<UserChildResponse> createChild (@RequestBody @Valid UserChildRequest userChildRequest) {
//    log.info("Start user registration {}", userChildRequest.getNickname());
//    UserChildResponse user = userService.createChild(userChildRequest);
//    log.info("Finalize user registration {}", userChildRequest.getNickname());
//
//    return  ResponseEntity.status(HttpStatus.CREATED).body(user);
//  }

}