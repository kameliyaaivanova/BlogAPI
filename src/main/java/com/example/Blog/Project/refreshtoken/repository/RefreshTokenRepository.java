package com.example.Blog.Project.refreshtoken.repository;

import com.example.Blog.Project.refreshtoken.model.RefreshToken;
import com.example.Blog.Project.user.model.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    <T> Optional<T> findByTokenAndUserAgentAndExpiresAtAfter(String token, String userAgent, Instant now, Class<T> type);

    <T> Optional<T> findByUserIdAndUserAgentAndExpiresAtAfter(Long id, String userAgent, Instant now, Class<T> type);

    boolean existsByOldToken(String token);

    void deleteByOldToken(String token);

    boolean existsByToken(String refreshToken);

    Optional<RefreshToken> findByToken(String refreshToken);

    void deleteByUser_Id(Long userId);
}
