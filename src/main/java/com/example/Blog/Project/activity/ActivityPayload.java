package com.example.Blog.Project.activity;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActivityPayload {

    @NotNull
    private String path;

    private Long userId;
}
