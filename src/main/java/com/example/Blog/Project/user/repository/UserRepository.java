package com.example.Blog.Project.user.repository;

import com.example.Blog.Project.user.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long>{
    boolean existsByUsernameOrEmail(String username, String email);
    
    Optional<User> findUserByUsername(String username);
}
