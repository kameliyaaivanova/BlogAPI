package com.example.Blog.Project.post.repository;

import com.example.Blog.Project.post.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    Optional<Post> findByTitle(String title);

    Page<Post> findByCategoriesId(Long categoryId, Pageable pageable);

    boolean existsByLogoContaining(UUID uuid);
}
