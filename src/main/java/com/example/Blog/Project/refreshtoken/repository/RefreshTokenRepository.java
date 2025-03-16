package com.example.Blog.Project.refreshtoken.repository;

import com.example.Blog.Project.refreshtoken.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    <T> Optional<T> findByTokenAndUserAgentAndExpiresAtAfter(String token, String userAgent, Instant now, Class<T> type);

    <T> Optional<T> findByUserIdAndUserAgentAndExpiresAtAfter(Long id, String userAgent, Instant now, Class<T> type);

    <T> Optional<T> findByOldToken(String token, Class<T> type);

    boolean existsByOldToken(String token);

    void deleteByOldToken(String token);
}
