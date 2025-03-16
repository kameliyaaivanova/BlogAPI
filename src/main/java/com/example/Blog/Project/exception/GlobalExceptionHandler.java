package com.example.Blog.Project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalError> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new GlobalError(
                        List.of(ex.getMessage()),
                        HttpStatus.BAD_REQUEST.value(),
                        Instant.now()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> message = new ArrayList<>();

        if (!ex.getAllErrors().isEmpty()) {
            for (ObjectError error : ex.getAllErrors()) {
                message.add(error.getDefaultMessage());
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new GlobalError(
                        message,
                        HttpStatus.BAD_REQUEST.value(),
                        Instant.now()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GlobalError> handleAccessIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new GlobalError(
                        List.of(ex.getMessage()),
                        HttpStatus.CONFLICT.value(),
                        Instant.now()
                ));
    }
}
