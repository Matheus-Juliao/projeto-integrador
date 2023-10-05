package com.unasp.taskmanagement.domain.task.api.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unasp.taskmanagement.config.component.MessageProperty;
import com.unasp.taskmanagement.config.service.TokenService;
import com.unasp.taskmanagement.domain.task.api.v1.request.TaskRequest;
import com.unasp.taskmanagement.domain.task.api.v1.response.TaskResponse;
import com.unasp.taskmanagement.domain.task.service.TaskService;
import com.unasp.taskmanagement.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
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

    @BeforeEach
    void setup() {
        httpHeaders = new HttpHeaders();
        mapper = new ObjectMapper();
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

    private TaskRequest getTaskRequest() {
        return TaskRequest.builder()
                .externalIdUserChild("63f37d4a-a4df-4beb-be6c-2f84695987c1")
                .name("To do the dishes")
                .reward(0.1)
                .description("Always wash dishes after eating")
                .build();
    }

    private TaskResponse getTaskResponse() {
        return TaskResponse.builder()
                .externalId("63f37d4a-a4df-4beb-be6c-2f84695987c8")
                .name("To do the dishes")
                .reward(0.1)
                .description("Always wash dishes after eating")
                .performed(false)
                .createdDate(LocalDateTime.now())
                .build();
    }

}
