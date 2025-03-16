package com.example.Blog.Project.security;

import com.example.Blog.Project.user.model.User;
import io.jsonwebtoken.Claims;
import lombok.EqualsAndHashCode;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.List;

@EqualsAndHashCode(exclude = { "claims", "principal" }, callSuper = false)
public class JwtAuthentication extends AbstractAuthenticationToken {

    private final String token;
    private final Claims claims;
    private final User principal;

    private final Collection<GrantedAuthority> authorities = new ArrayList<>();

    JwtAuthentication(final String token, final Claims claims) {
        super(null);
        this.token = token;
        this.claims = claims;
        this.principal = this.parseUser();
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return this.token;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    private User parseUser() {
        final User user = new User();

        if (Objects.nonNull(this.claims.get(JwtService.ID))) {
            user.setId(Long.parseLong(this.claims.get(JwtService.ID).toString()));
        }

        if (Objects.nonNull(this.claims.get(JwtService.EMAIL))) {
            user.setEmail(this.claims.get(JwtService.EMAIL).toString());
        }

        if (Objects.nonNull(this.claims.get(JwtService.USERNAME))) {
            user.setUsername(this.claims.get(JwtService.USERNAME).toString());
        }

        if (Objects.nonNull(this.claims.get(JwtService.PERMISSIONS))) {
            @SuppressWarnings("unchecked")
            List<String> permissions = (List<String>) this.claims.get(JwtService.PERMISSIONS);
            for (String permission : permissions) {
                this.authorities.add(new SimpleGrantedAuthority(permission));
            }
        }

        return user;
    }
}

