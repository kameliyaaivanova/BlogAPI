package com.example.Blog.Project.refreshtoken.model;

import com.example.Blog.Project.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class RefreshToken {

    @Id
    private Long id;

    @Size(min = 10, message = "Invalid token")
    @NotNull(message = "Token must be provided")
    @Column(nullable = false, unique = true)
    private String token;

    @Size(min = 10, message = "Invalid token")
    private String oldToken;

    @NotEmpty
    @Basic(optional = false)
    private String userAgent;

    @NotNull
    @Basic(optional = false)
    private Instant expiresAt;

    @OneToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "id")
    private User user;
}
