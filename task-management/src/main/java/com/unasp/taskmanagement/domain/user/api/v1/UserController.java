package com.unasp.taskmanagement.domain.user.api.v1;

import com.unasp.taskmanagement.config.messages.Messages;
import com.unasp.taskmanagement.domain.user.api.v1.request.UserChildRequest;
import com.unasp.taskmanagement.domain.user.api.v1.request.UserSponsorRequest;
import com.unasp.taskmanagement.domain.user.api.v1.response.UserChildResponse;
import com.unasp.taskmanagement.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
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
    log.info("Start user Sponsor registration {}", userSponsorRequest.getEmail());
    Messages message = userService.createSponsor(userSponsorRequest);
    log.info("Finalize user Sponsor registration {}", userSponsorRequest.getEmail());

    return  ResponseEntity.status(HttpStatus.CREATED).body(message);
  }

  @PostMapping("/child/new-user")
  @Operation(summary = "Register child", description = "Api for register an new child on the platform")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created", content =  { @Content(mediaType = "application/json", schema = @Schema(implementation = UserChildResponse.class)) }),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(hidden = true))),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true)))
  })
  public ResponseEntity<UserChildResponse> createChild (@RequestBody @Valid UserChildRequest userChildRequest) {
    log.info("Start user Child registration {}", userChildRequest.getNickname());
    UserChildResponse user = userService.createChild(userChildRequest);
    log.info("Finalize user Child registration {}", userChildRequest.getNickname());

    return  ResponseEntity.status(HttpStatus.CREATED).body(user);
  }

  @GetMapping("/list-child/{externalId}")
  @Operation(summary = "Returns all child of Sponsor", description = "API to list all child of Sponsor and their information on the platform")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Ok", content =  { @Content(mediaType = "application/json", schema = @Schema(implementation = UserChildResponse.class)) }),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(hidden = true))),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true)))
  })
  public ResponseEntity<List<UserChildResponse>> listChild(@PathVariable String externalId) {
    log.info("Start of list search {}", externalId);
    List<UserChildResponse> list = userService.listChild(externalId);
    log.info("Finalize of list search {}", externalId);

    return ResponseEntity.status(HttpStatus.OK).body(list);
  }

  @PutMapping("/update-child/{externalId}")
  @Operation(summary = "Update child",  description = "Api for update a child on the platform")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = UserChildResponse.class)) }),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(hidden = true))),
      @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
  })
  public ResponseEntity<UserChildResponse> updateChild(@PathVariable String externalId, @RequestBody @Valid UserChildRequest userChildRequest) {
    log.info("Start update child {}", externalId, userChildRequest.getNickname());
    UserChildResponse user = userService.updateChild(externalId, userChildRequest);
    log.info("Finalize update child {}", externalId, userChildRequest.getNickname());

    return ResponseEntity.status(HttpStatus.OK).body(user);
  }
}