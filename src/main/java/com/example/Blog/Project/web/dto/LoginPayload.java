package com.example.Blog.Project.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginPayload {

    @NotBlank(message = "Username cannot be empty!")
    @Size(min = 3, message = "Username length must be at least 3 characters!")
    private String username;

    @NotBlank(message = "Password cannot be empty!")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!$%@#£€*?&])[A-Za-z\\d!$%@#£€*?&]{8,}$",
            message = "Password must be at least 8 characters long, contain one uppercase, one lowercase, one digit, and one special character.")
    private String password;

    private String refreshToken;
}
