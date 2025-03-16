package com.example.Blog.Project.activity;

import lombok.Data;

import java.time.Instant;

@Data
public class Activity {

    private long id;

    private String path;

    private Long userId;

    private Instant createdAt;
}
