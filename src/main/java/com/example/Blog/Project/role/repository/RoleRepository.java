package com.example.Blog.Project.role.repository;

import com.example.Blog.Project.role.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByName(String name);

    Optional<Role> findById(long id);
}
