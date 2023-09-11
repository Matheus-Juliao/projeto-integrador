package com.unasp.taskmanagement.domain.user.api.v1.request;

import com.unasp.taskmanagement.domain.user.entity.User;
import com.unasp.taskmanagement.domain.user.enums.UserRole;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSponsorRequest {
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

    @NotNull(message = "readTerms is mandatory field")
    private Boolean readTerms;

    public User converter() {
        return User.builder()
            .externalId(UUID.randomUUID().toString())
            .name(name)
            .login(email)
            .password(new BCryptPasswordEncoder().encode(password))
            .role(UserRole.SPONSOR)
            .readTerms(readTerms)
            .active(true)
            .createdDate(LocalDateTime.now(ZoneId.of("UTC")))
            .build();
    }
}