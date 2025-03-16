package com.example.Blog.Project.file.repository;

import com.example.Blog.Project.file.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    Optional<File> findById(UUID uuid);

    void deleteById(UUID uuid);
}
