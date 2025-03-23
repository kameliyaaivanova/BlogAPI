package com.example.Blog.Project.web.dto;

import com.example.Blog.Project.permission.model.Permission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRolePayload {

    @NotBlank(message = "Title cannot be empty!")
    @Size(min = 3, message = "Title length must be at least 3 characters!")
    private String name;

    @NotEmpty(message = "At least 1 permission has to be selected!")
    private Set<Permission> permissions;
}
