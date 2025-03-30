package com.example.Blog.Project.activity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActivityPayload {

    private String path;

    private Long userId;
}
