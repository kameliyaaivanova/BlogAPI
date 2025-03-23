package com.example.Blog.Project.web.controller;

import com.example.Blog.Project.post.model.Post;
import com.example.Blog.Project.post.service.PostService;
import com.example.Blog.Project.web.dto.AddPostPayload;
import com.example.Blog.Project.web.dto.PresenceBody;
import com.example.Blog.Project.web.dto.UpdatePostPayload;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<Page<Post>> getAll(Pageable pageable, @RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(postService.getAll(pageable, categoryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getById(@PathVariable long id){
        return ResponseEntity.ok(postService.findPost(id));

    }

    @PostMapping("/add")
    public ResponseEntity<Post> create(@Valid @RequestBody AddPostPayload addPostPayload){
        return ResponseEntity.ok(postService.createPost(addPostPayload));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> update(@PathVariable long id, @Valid @RequestBody UpdatePostPayload updatePostPayload){
        return ResponseEntity.ok(postService.updatePost(id,updatePostPayload));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id){
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
