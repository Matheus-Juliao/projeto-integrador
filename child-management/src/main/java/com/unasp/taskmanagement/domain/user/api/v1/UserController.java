package com.unasp.taskmanagement.domain.user.api.v1;

import com.unasp.taskmanagement.config.component.MessageProperty;
import com.unasp.taskmanagement.domain.user.api.v1.request.UserRequest;
import com.unasp.taskmanagement.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/users")
@Slf4j
public class UserController {
  @Autowired
  private UserService userService;
  @PostMapping("/new-user")
  @Operation(summary = "Register user", description = "Api for register a new user on the platform")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created", content =  { @Content(mediaType = "application/json", schema = @Schema(implementation = MessageProperty.class)) }),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(hidden = true))),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
  })
  public ResponseEntity<String> create (@RequestBody @Valid UserRequest userRequest) {
    log.info("Start user registration {}", userRequest.getEmail());
    String message = userService.create(userRequest);
    log.info("Finalize user registration {}", userRequest.getEmail());
    return  ResponseEntity.status(HttpStatus.CREATED).body(message);
  }

}
