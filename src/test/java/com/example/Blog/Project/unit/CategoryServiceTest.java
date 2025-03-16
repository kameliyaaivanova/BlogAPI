package com.example.Blog.Project.unit;

import com.example.Blog.Project.category.model.Category;
import com.example.Blog.Project.category.repository.CategoryRepository;
import com.example.Blog.Project.category.service.CategoryService;
import com.example.Blog.Project.web.dto.AddCategoryPayload;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;


    @Test
    public void testGetAllCategories() {

        Pageable pageable = mock(Pageable.class);
        Category category = new Category();
        category.setTitle("Category 1");
        Page<Category> page = new PageImpl<>(List.of(category));

        when(categoryRepository.findAll(pageable)).thenReturn(page);

        Page<Category> result = categoryService.getAll(pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        verify(categoryRepository).findAll(pageable);
    }

    @Test
    public void testUpdateCategory_Success() {

        long categoryId = 1L;
        Category existingCategory = new Category();
        existingCategory.setTitle("Old Title");

        Category updatedCategory = new Category();
        updatedCategory.setTitle("New Title");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        Category result = categoryService.update(categoryId, updatedCategory);

        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    public void testUpdateCategory_NotFound_ShouldThrowException() {

        long categoryId = 1L;
        Category updatedCategory = new Category();
        updatedCategory.setTitle("New Title");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> categoryService.update(categoryId, updatedCategory));
        assertEquals("Category not found", exception.getMessage());

        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    public void testCreateCategory_Success() {

        AddCategoryPayload addCategoryPayload = new AddCategoryPayload();
        addCategoryPayload.setTitle("New Category");

        Category newCategory = new Category();
        newCategory.setTitle(addCategoryPayload.getTitle());

        when(categoryRepository.findByTitle(addCategoryPayload.getTitle())).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);

        Category result = categoryService.create(addCategoryPayload);

        assertNotNull(result);
        assertEquals("New Category", result.getTitle());
        verify(categoryRepository).findByTitle(addCategoryPayload.getTitle());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    public void testCreateCategory_TitleAlreadyExists_ShouldThrowException() {

        AddCategoryPayload addCategoryPayload = new AddCategoryPayload();
        addCategoryPayload.setTitle("Existing Category");

        Category existingCategory = new Category();
        existingCategory.setTitle("Existing Category");

        when(categoryRepository.findByTitle(addCategoryPayload.getTitle())).thenReturn(Optional.of(existingCategory));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> categoryService.create(addCategoryPayload));
        assertEquals("Category Title already exist!", exception.getMessage());

        verify(categoryRepository).findByTitle(addCategoryPayload.getTitle());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    public void testFindById_Success() {

        long categoryId = 1L;
        Category existingCategory = new Category();
        existingCategory.setTitle("Category 1");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));

        Category result = categoryService.findById(categoryId);

        assertNotNull(result);
        assertEquals("Category 1", result.getTitle());
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    public void testFindById_NotFound_ShouldThrowException() {

        long categoryId = 1L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> categoryService.findById(categoryId));
        assertEquals("Category with id [1] does not exist.", exception.getMessage());

        verify(categoryRepository).findById(categoryId);
    }
}
