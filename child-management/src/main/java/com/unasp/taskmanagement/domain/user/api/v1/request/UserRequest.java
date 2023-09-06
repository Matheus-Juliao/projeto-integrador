package com.unasp.taskmanagement.domain.user.api.v1.request;

import com.unasp.taskmanagement.domain.user.entity.User;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

//@Builder
@Data
//@NoArgsConstructor
//@AllArgsConstructor
public class UserRequest {
    @NotEmpty(message = "name is mandatory field")
    @Size(max = 100, message = "name field has a maximum size of 100 characters")
    private String name;

    @NotEmpty(message = "email is mandatory field")
    @Size(max = 100, message = "email field has a maximum size of 100 characters")
    @Email(message = "email invalid")
    private String email;

    @NotEmpty(message = "password is mandatory field")
    @Size(min = 5, max = 20, message = "password field has size minimum of 5 and a maximum of 20 characters")
    private String password;

    private Boolean readTerms;

    public User converter() {
        User user = new User();
        user.setExternalId(UUID.randomUUID().toString());
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setReadTerms(readTerms);
        user.setActive(true);
        user.setCreatedDate(LocalDateTime.now(ZoneId.of("UTC")));

        return user;
    }
}