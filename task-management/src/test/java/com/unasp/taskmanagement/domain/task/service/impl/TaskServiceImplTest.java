package com.unasp.taskmanagement.domain.task.service.impl;

import com.unasp.taskmanagement.config.component.MessageProperty;
import com.unasp.taskmanagement.config.messages.Messages;
import com.unasp.taskmanagement.config.service.TokenService;
import com.unasp.taskmanagement.domain.authentication.service.impl.AuthenticationServiceImpl;
import com.unasp.taskmanagement.domain.task.api.v1.request.NewCicleRequest;
import com.unasp.taskmanagement.domain.task.api.v1.request.TaskRequest;
import com.unasp.taskmanagement.domain.task.api.v1.request.TaskUpdateRequest;
import com.unasp.taskmanagement.domain.task.api.v1.response.TaskResponse;
import com.unasp.taskmanagement.domain.task.api.v1.response.TotalValueTasksPerformedResponse;
import com.unasp.taskmanagement.domain.task.entity.Task;
import com.unasp.taskmanagement.domain.task.repository.TaskRepository;
import com.unasp.taskmanagement.domain.user.entity.User;
import com.unasp.taskmanagement.domain.user.repository.UserRepository;
import com.unasp.taskmanagement.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

    @Mock
    private TaskRepository  taskRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MessageProperty messageProperty;
    @Mock
    private AuthenticationServiceImpl authenticationService;

    @InjectMocks
    private TaskServiceImpl taskService;

    private String externalId;

    @BeforeEach
    void setup() {
        externalId = "104dacaf-cd40-46f7-bb24-a7c5ee64ae59";
    }

    @Test
    void mustCreate() throws Exception {
        setUserChild();
        setUserAuthentication();
        when(authenticationService.getAuthenticatedUser()).thenReturn(getUser());

        TaskResponse taskResponse = taskService.create(getTaskRequest());
        verify(taskRepository, times(1)).save(any());
    }

    @Test
    void mustThrowsChildNotFound_WhenCreate() throws Exception {
        when(userRepository.findByExternalId(anyString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> taskService.create(getTaskRequest()));
        assertEquals(messageProperty.getProperty("error.notFound", messageProperty.getProperty("task")), exception.getMessage());
    }

    @Test
    void mustListAll() throws Exception {
        setUserChild();
        when(taskRepository.findByExternalIdUserChild(anyString())).thenReturn(List.of(getTask()));

        List<TaskResponse> list = taskService.listAll(externalId);

        assertEquals(list, List.of(getTaskResponse()));
        verify(taskRepository).findByExternalIdUserChild(externalId);
    }

    @Test
    void mustThrowsChildNotFound_WhenListAll() throws Exception {
        when(userRepository.findByExternalId(anyString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> taskService.listAll(externalId));
        assertEquals(messageProperty.getProperty("error.notFound", messageProperty.getProperty("task")), exception.getMessage());
    }

    @Test
    void mustTotalValueTasksPerformedResponse() throws Exception {
        setUserChild();

        when(taskRepository.totalValueTasksPerformed()).thenReturn(0.0);

        TotalValueTasksPerformedResponse total = taskService.totalValueTasksPerformed(externalId);
        assertEquals(total.getTotalValueTasksPerformed(), 0.0);
    }

    @Test
    void mustThrowsChildNotFound_WhenTotalValueTasksPerformedResponse() throws Exception {
        when(userRepository.findByExternalId(anyString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> taskService.totalValueTasksPerformed(externalId));
        assertEquals(messageProperty.getProperty("error.notFound", messageProperty.getProperty("task")), exception.getMessage());
    }

    @Test
    void mustUpdate() throws Exception {
        when(taskRepository.findByExternalId(anyString())).thenReturn(Optional.of(getTask()));

        TaskResponse taskResponse = taskService.update(externalId, getTaskUpdateRequest());

        verify(taskRepository, times(1)).save(any());
        assertEquals(taskResponse, getTaskUpdateResponse());
    }

    @Test
    void mustThrowsTaskNotFound_WhenUpdate() throws Exception {
        when(taskRepository.findByExternalId(anyString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> taskService.update(externalId, getTaskUpdateRequest()));
        assertEquals(messageProperty.getProperty("error.notFound", messageProperty.getProperty("task")), exception.getMessage());
    }

    @Test
    void mustDelete() throws Exception {
        when(taskRepository.findByExternalId(anyString())).thenReturn(Optional.of(getTask()));

        Messages messages = taskService.delete(externalId);
        verify(taskRepository, times(1)).delete(any());
        assertEquals(messageProperty.getProperty("success.delete", messageProperty.getProperty("task")), messages.getMessage());
    }

    @Test
    void mustThrowsTaskNotFound_WhenDelete() throws Exception {
        when(taskRepository.findByExternalId(anyString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> taskService.delete(externalId));
        assertEquals(messageProperty.getProperty("error.notFound", messageProperty.getProperty("task")), exception.getMessage());
    }

    @Test
    void mustNewCicle_WhenReuseTasksTrue() throws Exception {
        when(userRepository.findByExternalId(anyString())).thenReturn(Optional.of(getUser()));
        when(taskRepository.findByExternalIdUserChild(anyString())).thenReturn(List.of(getTask()));

        Messages messages = taskService.newCicle(getNewCicleRequest());
        verify(taskRepository, times(1)).saveAll(any());
        assertEquals(messageProperty.getProperty("success.newCicle"), messages.getMessage());
    }

    @Test
    void mustNewCicle_WhenReuseTasksFalse() throws Exception {
        when(userRepository.findByExternalId(anyString())).thenReturn(Optional.of(getUser()));

        NewCicleRequest newCicleRequest = getNewCicleRequest();
        newCicleRequest.setReuseTasks(false);

        Messages messages = taskService.newCicle(newCicleRequest);
        verify(taskRepository, times(1)).deleteExternalIdUserChild(anyString());
        assertEquals(messageProperty.getProperty("success.newCicle"), messages.getMessage());
    }

    @Test
    void mustThrowsChildNotFound_WhenNewCicle() throws Exception {
        when(userRepository.findByExternalId(anyString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> taskService.newCicle(getNewCicleRequest()));
        assertEquals(messageProperty.getProperty("error.notFound", messageProperty.getProperty("task")), exception.getMessage());
    }

    @Test
    void mustList() throws Exception {
        when(taskRepository.findByExternalId(anyString())).thenReturn(Optional.of(getTask()));

        TaskResponse taskResponse = taskService.list(externalId);
        assertEquals(taskResponse, getTaskResponse());
    }

    @Test
    void  mustThrowsTaskNotFound_WhenList() throws Exception {
        when(taskRepository.findByExternalId(anyString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class, () -> taskService.list(externalId));
        assertEquals(messageProperty.getProperty("error.notFound", messageProperty.getProperty("task")), exception.getMessage());
    }

    private void setUserChild() {
        when(userRepository.findByExternalId(anyString())).thenReturn(Optional.of(getUser()));
    }

    private void setUserAuthentication() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(getUser(), null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private User getUser() {
        return User.builder()
                .externalId(externalId)
                .name("Test")
                .login("email@test.com")
                .token(BCrypt.hashpw("token", BCrypt.gensalt()))
                .expiryDateToken(java.sql.Timestamp.valueOf(LocalDateTime.now().plusMinutes(10)))
                .build();
    }

    private Task getTask() {
        Task task = new Task();
        task.setExternalId(externalId);
        task.setName("To do the dishes");
        task.setReward(0.1);
        task.setDescription("Always wash dishes after eating");
        task.setPerformed(false);
        task.setCreatedDate(LocalDateTime.parse("2023-10-07T15:02:06.903595"));

        return task;
    }

    private TaskRequest getTaskRequest() {
        return TaskRequest.builder()
                .externalIdUserChild(externalId)
                .name("To do the dishes")
                .reward(0.1)
                .description("Always wash dishes after eating")
                .build();
    }

    private TaskResponse getTaskResponse() {
        return TaskResponse.builder()
                .externalId(externalId)
                .name("To do the dishes")
                .reward(0.1)
                .description("Always wash dishes after eating")
                .performed(false)
                .createdDate(LocalDateTime.parse("2023-10-07T15:02:06.903595"))
                .build();
    }

    private TaskResponse getTaskUpdateResponse() {
        return TaskResponse.builder()
            .externalId(externalId)
            .name("To do the dishes.")
            .reward(0.2)
            .description("Always wash dishes after eating all days")
            .performed(true)
            .createdDate(LocalDateTime.parse("2023-10-07T15:02:06.903595"))
            .build();
    }

    private TaskUpdateRequest getTaskUpdateRequest() {
        return TaskUpdateRequest.builder()
            .name("To do the dishes.")
            .reward(0.2)
            .description("Always wash dishes after eating all days")
            .performed(true)
            .build();
    }

    private NewCicleRequest getNewCicleRequest() {
        return NewCicleRequest.builder()
            .externalIdUserChild(externalId)
            .reuseTasks(true)
            .build();
    }

}
