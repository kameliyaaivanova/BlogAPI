package com.example.Blog.Project.security;

import com.example.Blog.Project.permission.model.Permission;
import com.example.Blog.Project.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtService {

    public static final long JWT_TOKEN_VALIDITY = 15L * 60 * 1000; // 15 minutes
    public static final long REFRESH_TOKEN_VALIDITY = 24L * 60 * 60 * 1000; // 1 day

    public static final String ISSUER = "blog";
    public static final String ID = "id";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String ROLE = "role";

    public static final String PERMISSIONS = "permissions";
    public static final String DATA = "data";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.refresh.token.secret}")
    private String refreshTokenSecret;

    private <T> T getClaimFromToken(final String token, final Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(decodeToken(token));
    }

    public Claims decodeToken(final String token) {
        return Jwts.parserBuilder().setSigningKey(secret.getBytes()).build().parseClaimsJws(token).getBody();
    }

    boolean isTokenExpired(final String token) {
        return getClaimFromToken(token, Claims::getExpiration).before(new Date());
    }

    public String generateToken(final User user) {
        final Map<String, Object> claims = new HashMap<>();
        claims.put(ID, user.getId());
        claims.put(USERNAME, user.getUsername());
        claims.put(EMAIL, user.getEmail());
        claims.put(ROLE, user.getRole().getName());
        claims.put(PERMISSIONS, user.getRole().getPermissions().stream().map(Permission::getTitle).toList());

        return generate(claims, secret, JWT_TOKEN_VALIDITY);
    }

    public String generateRefreshToken() {
        final Map<String, Object> claims = new HashMap<>();
        claims.put(DATA, UUID.randomUUID());

        return generate(claims, refreshTokenSecret, REFRESH_TOKEN_VALIDITY);
    }

    private String generate(final Map<String, Object> claims, final String secret, long validityMs) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(ISSUER)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + validityMs))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS512)
                .compact();
    }
}