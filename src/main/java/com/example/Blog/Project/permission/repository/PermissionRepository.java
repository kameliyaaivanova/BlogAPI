package com.example.Blog.Project.permission.repository;

import com.example.Blog.Project.permission.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findOneByTitle(String title);
}
