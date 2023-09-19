package com.unasp.taskmanagement.domain.task.api.v1.response;

import com.unasp.taskmanagement.domain.task.entity.Task;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskResponse {
  private String externalId;
  private String name;
  private Double reward;
  private String description;
  private Boolean performed;
  private LocalDateTime createdDate;

  public TaskResponse converter(Task task) {
    return TaskResponse.builder()
        .externalId(task.getExternalId())
        .name(task.getName())
        .reward(task.getReward())
        .description(task.getDescription())
        .performed(task.isPerformed())
        .createdDate(task.getCreatedDate())
        .build();
  }
}