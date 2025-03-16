package com.example.Blog.Project.web.controller;

import com.example.Blog.Project.category.model.Category;
import com.example.Blog.Project.category.service.CategoryService;
import com.example.Blog.Project.web.dto.AddCategoryPayload;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<Page<Category>> getAll(Pageable pageable) {
        return ResponseEntity.ok(categoryService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> findById(@PathVariable long id){
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> update(@PathVariable long id, @Valid @RequestBody Category category){
        return ResponseEntity.ok(categoryService.update(id,category));
    }

    @PostMapping("/add")
    public ResponseEntity<Category> create(@RequestBody @Valid AddCategoryPayload addCategoryPayload){
        return ResponseEntity.ok(categoryService.create(addCategoryPayload));
    }
}
