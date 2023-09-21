package com.unasp.taskmanagement.domain.task.api.v1.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewCicleRequest {
  @NotEmpty(message = "externalIdUserChild is mandatory field")
  @Size(max = 100, message = "externalIdUserChild field has a maximum size of 100 characters")
  private String externalIdUserChild;

  @NotNull(message = "reuseTasks is mandatory field")
  private Boolean reuseTasks;
}