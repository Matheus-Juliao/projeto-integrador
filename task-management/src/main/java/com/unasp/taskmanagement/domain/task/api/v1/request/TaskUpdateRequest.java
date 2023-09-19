package com.unasp.taskmanagement.domain.task.api.v1.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TaskUpdateRequest {
  @NotEmpty(message = "name is mandatory field")
  @Size(max = 100, message = "name field has a maximum size of 100 characters")
  private String name;

  @NotNull(message = "reward is mandatory field")
  private Double reward;

  @NotEmpty(message = "description is mandatory field")
  @Size(max = 300, message = "name field has a maximum size of 300 characters")
  private String description;

  @NotNull(message = "performed is mandatory field")
  private Boolean performed;
}