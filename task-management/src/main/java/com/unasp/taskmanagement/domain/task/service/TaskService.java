package com.unasp.taskmanagement.domain.task.service;

import com.unasp.taskmanagement.config.messages.Messages;
import com.unasp.taskmanagement.domain.task.api.v1.request.TaskRequest;
import com.unasp.taskmanagement.domain.task.api.v1.request.TaskUpdateRequest;
import com.unasp.taskmanagement.domain.task.api.v1.response.TaskResponse;
import com.unasp.taskmanagement.domain.task.api.v1.response.TotalValueTasksPerformedResponse;
import java.util.List;

public interface TaskService {
  TaskResponse create(TaskRequest taskRequest);
  List<TaskResponse> listTask(String externalId);
  TotalValueTasksPerformedResponse totalValueTasksPerformed(String externalId);
  TaskResponse update(String externalId, TaskUpdateRequest taskUpdateRequest);
  Messages delete(String externalId);
}