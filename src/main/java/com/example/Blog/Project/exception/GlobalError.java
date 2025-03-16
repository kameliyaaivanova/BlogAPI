package com.example.Blog.Project.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
public class GlobalError {

    private List<String> message;

    private long code;

    private Instant timestamp;
}
