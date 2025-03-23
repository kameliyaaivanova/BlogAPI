package com.example.Blog.Project.web.controller;

import com.example.Blog.Project.user.model.AuthResponse;
import com.example.Blog.Project.user.model.User;
import com.example.Blog.Project.user.service.UserService;
import com.example.Blog.Project.web.dto.AddUserPayload;
import com.example.Blog.Project.web.dto.LoginPayload;
import com.example.Blog.Project.web.dto.RegisterPayload;
import com.example.Blog.Project.web.dto.UpdateUserPayload;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginPayload loginPayload, @RequestHeader(value = "User-Agent", required = false) final String userAgent) {
        return ResponseEntity.ok(this.userService.authenticate(loginPayload, userAgent));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterPayload payload) {

        boolean isRegistered = userService.register(payload);

        if (!isRegistered) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public ResponseEntity<Page<User>> getAll(Pageable pageable) {
        System.out.println("User Controller Invoker!!!!!");
        return ResponseEntity.ok(userService.getAll(pageable));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getById(@PathVariable long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> update(@PathVariable long id, @Valid @RequestBody UpdateUserPayload updateUserPayload) {
        return ResponseEntity.ok(userService.update(id, updateUserPayload));
    }

    @PostMapping("/users/add")
    public ResponseEntity<User> create(@Valid @RequestBody AddUserPayload addUserPayload) {
        return ResponseEntity.ok(userService.create(addUserPayload));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleUserNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
