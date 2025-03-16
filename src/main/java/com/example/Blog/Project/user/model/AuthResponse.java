package com.example.Blog.Project.user.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AuthResponse {

    @NotBlank
    private String token;

    @NotBlank
    private String refreshToken;
}
