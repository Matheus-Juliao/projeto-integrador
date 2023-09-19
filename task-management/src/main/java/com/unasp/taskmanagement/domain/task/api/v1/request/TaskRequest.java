package com.unasp.taskmanagement.domain.task.api.v1.request;

import com.unasp.taskmanagement.domain.task.entity.Task;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
  @NotEmpty(message = "externalIdUserChild is mandatory field")
  @Size(max = 100, message = "externalIdUserChild field has a maximum size of 100 characters")
  private String externalIdUserChild;

  @NotEmpty(message = "name is mandatory field")
  @Size(max = 100, message = "name field has a maximum size of 100 characters")
  private String name;

  @NotNull(message = "reward is mandatory field")
  private Double reward;

  @NotEmpty(message = "description is mandatory field")
  @Size(max = 300, message = "name field has a maximum size of 300 characters")
  private String description;

  public Task converter(String externalIdUserSponsor) {
    return Task.builder()
        .externalId(UUID.randomUUID().toString())
        .name(name)
        .reward(reward)
        .description(description)
        .performed(false)
        .createdDate(LocalDateTime.now(ZoneId.of("UTC")))
        .externalIdUserSponsor(externalIdUserSponsor)
        .externalIdUserChild(externalIdUserChild)
        .build();
  }
}
