package com.unasp.taskmanagement.domain.task.api.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unasp.taskmanagement.config.component.MessageProperty;
import com.unasp.taskmanagement.config.messages.Messages;
import com.unasp.taskmanagement.config.service.TokenService;
import com.unasp.taskmanagement.domain.task.api.v1.request.NewCicleRequest;
import com.unasp.taskmanagement.domain.task.api.v1.request.TaskRequest;
import com.unasp.taskmanagement.domain.task.api.v1.request.TaskUpdateRequest;
import com.unasp.taskmanagement.domain.task.api.v1.response.TaskResponse;
import com.unasp.taskmanagement.domain.task.api.v1.response.TotalValueTasksPerformedResponse;
import com.unasp.taskmanagement.domain.task.service.TaskService;
import com.unasp.taskmanagement.domain.user.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TaskController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    TaskService taskService;
    @MockBean
    private MessageProperty messageProperty;

    @MockBean
    private TokenService tokenService;

    @MockBean
    UserRepository userRepository;

    private HttpHeaders httpHeaders;
    private final String urlBase = "/v1/task";
    private String externalId;

    @BeforeEach
    void setup() {
        externalId = "104dacaf-cd40-46f7-bb24-a7c5ee64ae59";
        mapper = new ObjectMapper();
        httpHeaders = new HttpHeaders();
    }

    @Test
    void mustCreate() throws Exception {
        when(taskService.create(any())).thenReturn(getTaskResponse());

        mockMvc.perform(MockMvcRequestBuilders
            .post(urlBase)
            .headers(httpHeaders)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(getTaskRequest())))
            .andExpect(status().isCreated());
    }

    @Test
    void mustListAll() throws Exception {
        when(taskService.listAll(anyString())).thenReturn(List.of(getTaskResponse()));

        mockMvc.perform(MockMvcRequestBuilders
            .get(urlBase + "/list-all/{externalId}", externalId)
            .headers(httpHeaders)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void mustTotalValueTasksPerformed() throws Exception {
        when(taskService.totalValueTasksPerformed(anyString())).thenReturn(getTotalValueTasksPerformed());

        mockMvc.perform(MockMvcRequestBuilders
            .get(urlBase + "/total-value-tasks-performed/{externalId}", externalId)
            .headers(httpHeaders)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void mustUpdate() throws Exception {
        when(taskService.update(anyString(), any())).thenReturn(getTaskResponse());

        mockMvc.perform(MockMvcRequestBuilders
            .put(urlBase + "/{externalId}", externalId)
            .headers(httpHeaders)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(getTaskUpdateRequest())))
            .andExpect(status().isOk());
    }

    @Test
    void mustDelete() throws Exception {
        when(taskService.delete(anyString())).thenReturn(Messages.builder().build());

        mockMvc.perform(MockMvcRequestBuilders
            .delete(urlBase + "/{externalId}", externalId)
            .headers(httpHeaders)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @Test
    void mustNewCicle() throws Exception {
        when(taskService.newCicle(any())).thenReturn(getMessage());

        mockMvc.perform(MockMvcRequestBuilders
                .post(urlBase + "/new-cicle")
                .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(getNewCicleRequest())))
                .andExpect(status().isOk());
    }

    @Test
    void mustList() throws Exception {
        when(taskService.list(anyString())).thenReturn(getTaskResponse());

        mockMvc.perform(MockMvcRequestBuilders
                .get(urlBase + "/{externalId}", externalId)
                .headers(httpHeaders)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
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
                .createdDate(LocalDateTime.now())
                .build();
    }

    private TaskUpdateRequest getTaskUpdateRequest() {
        return TaskUpdateRequest.builder()
            .name("To do the dishes")
            .reward(0.1)
            .description("Always wash dishes after eating")
            .performed(true)
            .build();
    }

    private TotalValueTasksPerformedResponse getTotalValueTasksPerformed() {
        return TotalValueTasksPerformedResponse.builder()
            .totalValueTasksPerformed(0.0)
            .build();
    }

    private Messages getMessage() {
        return Messages.builder().build();
    }

    private NewCicleRequest getNewCicleRequest() {
        return NewCicleRequest.builder()
            .externalIdUserChild(externalId)
            .reuseTasks(false)
            .build();
    }

}
