package com.example.Blog.Project.category.service;

import com.example.Blog.Project.category.model.Category;
import com.example.Blog.Project.category.repository.CategoryRepository;
import com.example.Blog.Project.web.dto.AddCategoryPayload;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Page<Category> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    public Category update(long id, Category updatedCategory) {
        return categoryRepository.findById(id).map(category -> {
            category.setTitle(updatedCategory.getTitle());
            return categoryRepository.save(category);
        }).orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    public Category create(AddCategoryPayload addCategoryPayload) {
        Optional<Category> byTitle = this.categoryRepository.findByTitle(addCategoryPayload.getTitle());

        if (byTitle.isPresent()){
            throw new IllegalArgumentException("Category Title already exist!");
        }

        Category newCategory = new Category();
        newCategory.setTitle(addCategoryPayload.getTitle());

        return categoryRepository.save(newCategory);
    }

    public Category findById(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id [%d] does not exist.".formatted(id)));
    }
}
