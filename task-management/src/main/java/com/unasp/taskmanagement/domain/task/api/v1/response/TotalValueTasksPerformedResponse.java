package com.unasp.taskmanagement.domain.task.api.v1.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TotalValueTasksPerformedResponse {
  private double totalValueTasksPerformed;

  public TotalValueTasksPerformedResponse converter(Double totalValueTasksPerformed) {
    return TotalValueTasksPerformedResponse.builder()
        .totalValueTasksPerformed(totalValueTasksPerformed)
        .build();
  }
}