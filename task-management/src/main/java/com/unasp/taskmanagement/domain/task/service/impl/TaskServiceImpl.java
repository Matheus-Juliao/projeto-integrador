package com.unasp.taskmanagement.domain.task.service.impl;

import com.unasp.taskmanagement.config.component.MessageProperty;
import com.unasp.taskmanagement.config.messages.Messages;
import com.unasp.taskmanagement.domain.authentication.service.AuthenticationService;
import com.unasp.taskmanagement.domain.task.api.v1.request.NewCicleRequest;
import com.unasp.taskmanagement.domain.task.api.v1.request.TaskRequest;
import com.unasp.taskmanagement.domain.task.api.v1.request.TaskUpdateRequest;
import com.unasp.taskmanagement.domain.task.api.v1.response.TaskResponse;
import com.unasp.taskmanagement.domain.task.api.v1.response.TotalValueTasksPerformedResponse;
import com.unasp.taskmanagement.domain.task.entity.Task;
import com.unasp.taskmanagement.domain.task.repository.TaskRepository;
import com.unasp.taskmanagement.domain.task.service.TaskService;
import com.unasp.taskmanagement.domain.user.repository.UserRepository;
import com.unasp.taskmanagement.exception.NotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl implements TaskService {
  @Autowired
  TaskRepository taskRepository;
  @Autowired
  UserRepository userRepository;
  @Autowired
  MessageProperty messageProperty;
  @Autowired
  AuthenticationService authenticationService;

  @Transactional
  @Override
  public TaskResponse create(TaskRequest taskRequest) {
    checkIfThereIsChild(taskRequest.getExternalIdUserChild());
    Task task = taskRequest.converter(authenticationService.getAuthenticatedUser().getExternalId());
    taskRepository.save(task);

    return TaskResponse.builder().build().converter(task);
  }

  @Override
  public List<TaskResponse> listAll(String externalId) {
    checkIfThereIsChild(externalId);

    return taskRepository.findByExternalIdUserChild(externalId)
        .stream().map(task -> TaskResponse.builder()
            .externalId(task.getExternalId())
            .name(task.getName())
            .reward(task.getReward())
            .description(task.getDescription())
            .performed(task.isPerformed())
            .createdDate(task.getCreatedDate())
            .build())
        .collect(Collectors.toList());
  }

  @Override
  public TotalValueTasksPerformedResponse totalValueTasksPerformed(String externalId) {
    checkIfThereIsChild(externalId);
    Double total = taskRepository.totalValueTasksPerformed(externalId);
    total = total == null ? 0.0 : total;

    return TotalValueTasksPerformedResponse.builder().build().converter(total);
  }

  @Transactional
  @Override
  public TaskResponse update(String externalId, TaskUpdateRequest taskUpdateRequest) {
    Task task = getTask(externalId);
    BeanUtils.copyProperties(taskUpdateRequest, task);
    task.setUpdatedDate(LocalDateTime.now(ZoneId.of("UTC")));
    taskRepository.save(task);

    return TaskResponse.builder().build().converter(task);
  }

  @Transactional
  @Override
  public Messages delete(String externalId) {
    Task task = getTask(externalId);
    taskRepository.delete(task);

    return Messages.builder().build().converter(messageProperty.getProperty("success.delete", messageProperty.getProperty("task")), HttpStatus.OK.value());
  }

  @Transactional
  @Override
  public Messages newCicle(NewCicleRequest newCicleRequest) {
    checkIfThereIsChild(newCicleRequest.getExternalIdUserChild());
    if(newCicleRequest.getReuseTasks()) {
      List<Task> taskList = taskRepository.findByExternalIdUserChild(newCicleRequest.getExternalIdUserChild());
      List<Task> updatedTaskList = taskList.stream().peek(task -> task.setPerformed(false)).collect(Collectors.toList());
      taskRepository.saveAll(updatedTaskList);
    } else {
      taskRepository.deleteExternalIdUserChild(newCicleRequest.getExternalIdUserChild());
    }

    return Messages.builder().build().converter(messageProperty.getProperty("success.newCicle"), HttpStatus.OK.value());
  }

  @Override
  public TaskResponse list(String externalId) {
    Task task = getTask(externalId);

    return TaskResponse.builder().build().converter(task);
  }

  private void checkIfThereIsChild(String externalId) {
    if(userRepository.findByExternalId(externalId).isEmpty()) {
      throw new NotFoundException(messageProperty.getProperty("error.notFound", messageProperty.getProperty("child")));
    }
  }

  private Task getTask(String externalId) {
    Optional<Task> taskOptional = taskRepository.findByExternalId(externalId);
    if(taskOptional.isEmpty()) {
      throw new NotFoundException(messageProperty.getProperty("error.notFound", messageProperty.getProperty("task")));
    }

    return taskOptional.get();
  }
}