package com.example.Blog.Project.web.dto;

import com.example.Blog.Project.role.model.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserPayload {

    @NotBlank(message = "Username cannot be empty!")
    private String username;

    @NotEmpty(message = "Email cannot be empty!")
    @Email(message = "Email must be valid!")
    private String email;

    @NotBlank(message = "Password cannot be empty!")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!$%@#£€*?&])[A-Za-z\\d!$%@#£€*?&]{8,}$",
            message = "Password must be at least 8 characters long, contain one uppercase, one lowercase, one digit, and one special character.")
    private String password;

    @NotNull(message = "Role cannot be empty!")
    private Role role;
}
