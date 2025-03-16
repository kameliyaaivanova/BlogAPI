package com.example.Blog.Project.post.model;

import com.example.Blog.Project.category.model.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String author;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @NotBlank
    private String logo;

    @ManyToMany
    private Set<Category> categories;

    public Post(String testTitle, String testContent, String testDescription, String testLogo, Object o) {
    }
}
