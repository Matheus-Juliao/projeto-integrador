package com.unasp.taskmanagement.domain.task.api.v1;

import com.unasp.taskmanagement.config.messages.Messages;
import com.unasp.taskmanagement.domain.task.api.v1.request.TaskRequest;
import com.unasp.taskmanagement.domain.task.api.v1.request.TaskUpdateRequest;
import com.unasp.taskmanagement.domain.task.api.v1.response.TaskResponse;
import com.unasp.taskmanagement.domain.task.api.v1.response.TotalValueTasksPerformedResponse;
import com.unasp.taskmanagement.domain.task.service.TaskService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/task")
@Slf4j
@SuppressWarnings("unused")
public class TaskController {
  @Autowired
  TaskService taskService;

  @PostMapping()
  @Operation(summary = "Register new task", description = "Api for register a new task for a child on the platform")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Created", content =  { @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class)) }),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(hidden = true))),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true)))
  })
  public ResponseEntity<TaskResponse> create (@RequestBody @Valid TaskRequest taskRequest) {
    log.info("Start task registration {}", taskRequest.getName());
    TaskResponse taskResponse = taskService.create(taskRequest);
    log.info("Finalize task registration {}", taskRequest.getName());

    return  ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
  }

  @GetMapping("/{externalId}")
  @Operation(summary = "List task", description = "List a child's tasks")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Ok", content =  { @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class)) }),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(hidden = true))),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true)))
  })
  public ResponseEntity<List<TaskResponse>> listTask (@PathVariable String externalId) {
    log.info("Start listing tasks performed {}", externalId);
    List<TaskResponse> list = taskService.listTask(externalId);
    log.info("Finalize listing tasks performed {}", externalId);

    return  ResponseEntity.status(HttpStatus.OK).body(list);
  }

  @GetMapping("/total-value-tasks-performed/{externalId}")
  @Operation(summary = "List total value of tasks performed", description = "List the total value of tasks performed by a child")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Ok", content =  { @Content(mediaType = "application/json", schema = @Schema(implementation = TotalValueTasksPerformedResponse.class)) }),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(hidden = true))),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true)))
  })
  public ResponseEntity<TotalValueTasksPerformedResponse> totalValueTasksPerformed (@PathVariable String externalId) {
    log.info("Start list total value of tasks performed {}", externalId);
    TotalValueTasksPerformedResponse total = taskService.totalValueTasksPerformed(externalId);
    log.info("Finalize list total value of tasks performed {}", externalId);

    return  ResponseEntity.status(HttpStatus.OK).body(total);
  }

  @PutMapping("/{externalId}")
  @Operation(summary = "Update task",  description = "Api for update a child's task on the platform")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = TaskUpdateRequest.class)) }),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(hidden = true))),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true)))
  })
  public ResponseEntity<TaskResponse> update(@PathVariable String externalId, @RequestBody @Valid TaskUpdateRequest taskUpdateRequest) {
    log.info("Start update task {}", externalId);
    TaskResponse task = taskService.update(externalId, taskUpdateRequest);
    log.info("Finalize update task {}", externalId);

    return ResponseEntity.status(HttpStatus.OK).body(task);
  }

  @DeleteMapping("/{externalId}")
  @Operation(summary = "Delete a task", description = "API to delete child's task on the platform")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = Messages.class))),
      @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(schema = @Schema(hidden = true))),
      @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(hidden = true)))
  })
  public ResponseEntity<Messages> delete(@PathVariable String externalId) {
    log.info("Start delete task {}", externalId);
    Messages message = taskService.delete(externalId);
    log.info("Finalize task child {}", externalId);

    return ResponseEntity.status(HttpStatus.OK).body(message);
  }
}