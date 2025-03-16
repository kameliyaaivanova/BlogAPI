package com.example.Blog.Project.integration.data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import com.example.Blog.Project.permission.model.Permission;
import com.example.Blog.Project.permission.model.PermissionOption;
import com.example.Blog.Project.permission.repository.PermissionRepository;
import com.example.Blog.Project.refreshtoken.model.RefreshToken;
import com.example.Blog.Project.refreshtoken.repository.RefreshTokenRepository;
import com.example.Blog.Project.role.model.Role;
import com.example.Blog.Project.role.repository.RoleRepository;
import com.example.Blog.Project.user.model.User;
import com.example.Blog.Project.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Seeder {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User user(String username, String email, String password, Role role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setEnabled(true);
        user.setRole(role);

        return userRepository.save(user);
    }

    public List<Permission> permissions(PermissionOption... permissions) {
        List<Permission> result = new ArrayList<>();

        if (permissions == null || permissions.length == 0) {
            permissions = PermissionOption.values();
        }

        for (PermissionOption permissionOption : permissions) {
            Optional<Permission> permissionOptional = permissionRepository.findOneByTitle(permissionOption.getAbbreviation());
            if (permissionOptional.isEmpty()) {
                Permission permission = new Permission();
                permission.setTitle(permissionOption.getAbbreviation());
                permission.setDescription(permissionOption.getDescription());
                result.add(permissionRepository.save(permission));
            } else {
                result.add(permissionOptional.get());
            }
        }

        return result;
    }

    public Role role(String name, List<Permission> permissions) {
        Role role = new Role();
        role.setName(name);
        role.setPermissions(new HashSet<>(permissions));

        return roleRepository.save(role);
    }

    public RefreshToken refreshToken(String token, Instant expiresAt, User user, String oldToken) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setOldToken(oldToken);
        refreshToken.setExpiresAt(expiresAt);
        refreshToken.setUserAgent("user-agent");
        refreshToken.setUser(user);

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken refreshTokenTest(String token, Instant expiresAt, User user, String oldToken) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setOldToken(oldToken);
        refreshToken.setExpiresAt(expiresAt);
        refreshToken.setUserAgent("user-agent");
        refreshToken.setUser(user);

        return refreshToken;
    }

    public void clearData() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        permissionRepository.deleteAll();
    }
}
