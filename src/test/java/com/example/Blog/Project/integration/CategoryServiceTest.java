package com.example.Blog.Project.integration;

import com.example.Blog.Project.category.model.Category;
import com.example.Blog.Project.category.repository.CategoryRepository;
import com.example.Blog.Project.category.service.CategoryService;
import com.example.Blog.Project.web.dto.AddCategoryPayload;
import com.example.Blog.Project.web.dto.UpdateCategoryPayload;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CategoryServiceTest extends BaseTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void setup() {
        categoryRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "testuser", authorities = "c.r")
    public void testGetAll_ShouldReturnEmpty_WhenNoCategoriesExist() {
        Page<Category> categoryPage = categoryService.getAll(PageRequest.of(0, 10));
        assertThat(categoryPage.getTotalElements()).isEqualTo(0);
    }

    @Test
    @WithMockUser(username = "testuser", roles = "c.r")
    public void testGetAll_ShouldReturnCategories_WhenCategoriesExist() {
        Category category1 = new Category();
        category1.setTitle("Technology");
        categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setTitle("Health");
        categoryRepository.save(category2);

        Page<Category> categoryPage = categoryService.getAll(PageRequest.of(0, 10));

        assertThat(categoryPage.getTotalElements()).isEqualTo(2);
        assertThat(categoryPage.getContent()).hasSize(2);
    }

    @Test
    @WithMockUser(username = "testuser", authorities = "c.u")
    public void testUpdate_ShouldUpdateCategory_WhenCategoryExists() {
        Category category = new Category();
        category.setTitle("Old Category");
        Category savedCategory = categoryRepository.save(category);

        UpdateCategoryPayload updatePayload = new UpdateCategoryPayload();
        updatePayload.setTitle("Updated Category");

        Category updatedCategory = categoryService.update(savedCategory.getId(), updatePayload);

        assertThat(updatedCategory.getTitle()).isEqualTo("Updated Category");
    }

    @Test
    @WithMockUser(username = "testuser", authorities = "c.u")
    public void testUpdate_ShouldThrowException_WhenCategoryDoesNotExist() {
        long invalidCategoryId = 999L;
        UpdateCategoryPayload updatePayload = new UpdateCategoryPayload();
        updatePayload.setTitle("New Title");

        assertThrows(EntityNotFoundException.class, () -> categoryService.update(invalidCategoryId, updatePayload));
    }

    @Test
    @WithMockUser(username = "testuser", authorities = "c.c")
    public void testCreate_ShouldCreateCategory_WhenCategoryTitleIsUnique() {
        AddCategoryPayload addCategoryPayload = new AddCategoryPayload();
        addCategoryPayload.setTitle("New Category");

        Category createdCategory = categoryService.create(addCategoryPayload);

        assertThat(createdCategory).isNotNull();
        assertThat(createdCategory.getTitle()).isEqualTo("New Category");
        assertThat(categoryRepository.existsById(createdCategory.getId())).isTrue();
    }

    @Test
    @WithMockUser(username = "testuser", authorities = "c.c")
    public void testCreate_ShouldThrowException_WhenCategoryTitleAlreadyExists() {
        Category existingCategory = new Category();
        existingCategory.setTitle("Duplicate Category");
        categoryRepository.save(existingCategory);

        AddCategoryPayload addCategoryPayload = new AddCategoryPayload();
        addCategoryPayload.setTitle("Duplicate Category");

        assertThrows(IllegalArgumentException.class, () -> categoryService.create(addCategoryPayload));
    }

    @Test
    @WithMockUser(username = "testuser", roles = "c.r")
    public void testFindById_ShouldReturnCategory_WhenCategoryExists() {
        Category category = new Category();
        category.setTitle("Some Category");
        Category savedCategory = categoryRepository.save(category);

        Category foundCategory = categoryService.findById(savedCategory.getId());

        assertThat(foundCategory).isNotNull();
        assertThat(foundCategory.getTitle()).isEqualTo("Some Category");
    }

    @Test
    @WithMockUser(username = "testuser", authorities = "c.r")
    public void testFindById_ShouldThrowException_WhenCategoryDoesNotExist() {
        long invalidCategoryId = 999L;
        assertThrows(EntityNotFoundException.class, () -> categoryService.findById(invalidCategoryId));
    }
}
