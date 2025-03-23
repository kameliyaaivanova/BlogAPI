package com.example.Blog.Project.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCategoryPayload {

    @NotBlank(message = "Title cannot be empty!")
    @Size(min = 3, message = "Title length must be at least 3 characters!")
    private String title;
}
