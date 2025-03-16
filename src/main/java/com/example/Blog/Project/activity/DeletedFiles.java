package com.example.Blog.Project.activity;

import lombok.Data;

import java.time.Instant;

@Data
public class DeletedFiles {

    private long id;

    private Long amount;

    private Instant createdAt;
}
