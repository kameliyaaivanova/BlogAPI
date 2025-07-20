package com.example.Blog.Project.post.repository;

import com.example.Blog.Project.post.model.Post;
import com.example.Blog.Project.post.model.PostLike;
import com.example.Blog.Project.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike,Long> {

    Optional<PostLike> findByUserAndPost(User user, Post post);
}
