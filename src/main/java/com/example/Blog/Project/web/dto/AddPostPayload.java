package com.example.Blog.Project.web.dto;

import com.example.Blog.Project.category.model.Category;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddPostPayload {

    @NotBlank(message = "Title cannot be empty!")
    @Size(min = 3 ,message = "Title length must be at least 3 characters!")
    private String title;

    @NotBlank(message = "Description cannot be empty!")
    @Size(min = 3 ,message = "Description length must be at least 3 characters!")
    private String description;

    @NotBlank(message = "Logo cannot be empty!")
    private String logo;

    @NotBlank(message = "Text cannot be empty!")
    @Size(min = 5, message = "Text length must be at least 5 characters!")
    private String content;

    @NotNull(message = "At least 1 category has to be selected!")
    private Set<Category> categories;

}
