package com.example.Blog.Project.user.service;

import com.example.Blog.Project.exception.AuthenticationFailedException;
import com.example.Blog.Project.refreshtoken.model.RefreshToken;
import com.example.Blog.Project.refreshtoken.repository.RefreshTokenRepository;
import com.example.Blog.Project.role.model.Role;
import com.example.Blog.Project.role.repository.RoleRepository;
import com.example.Blog.Project.security.JwtService;
import com.example.Blog.Project.user.model.AuthResponse;
import com.example.Blog.Project.user.model.MyUserDetails;
import com.example.Blog.Project.user.model.User;
import com.example.Blog.Project.user.repository.UserRepository;
import com.example.Blog.Project.web.dto.AddUserPayload;
import com.example.Blog.Project.web.dto.LoginPayload;
import com.example.Blog.Project.web.dto.RegisterPayload;
import com.example.Blog.Project.web.dto.UpdateUserPayload;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public UserService(
            UserRepository userRepository,
            ModelMapper modelMapper,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository,
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findUserByUsername(username);

        if (userOpt.isEmpty()) {
            throw new EntityNotFoundException("Could not find user");
        }

        return new MyUserDetails(userOpt.get());
    }

    public boolean register(RegisterPayload registerPayload){
        boolean existsByUsernameOrEmail = this.userRepository.existsByUsernameOrEmail(registerPayload.getUsername(), registerPayload.getEmail());

        if (existsByUsernameOrEmail) {
            throw new IllegalArgumentException("Username or email already exists.");
        }

        Optional<Role> userRoleOpt = roleRepository.findByName("User");

        User user = modelMapper.map(registerPayload,User.class);
        user.setPassword(passwordEncoder.encode(registerPayload.getPassword()));
        user.setRole(userRoleOpt.get());
        user.setEnabled(true);
        userRepository.save(user);

        return true;
    }

    public Page<User> getAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(dontRollbackOn = AuthenticationFailedException.class)
    public AuthResponse authenticate(final LoginPayload payload, final String userAgent) {
        User user = null;
        RefreshToken refreshToken = null;

        if (StringUtils.hasText(payload.getRefreshToken())) {

            boolean matchPreviousToken = refreshTokenRepository.existsByOldToken(payload.getRefreshToken());

            if (matchPreviousToken) {
                refreshTokenRepository.deleteByOldToken(payload.getRefreshToken());

                throw new AuthenticationFailedException("Invalid credentials");
            }

            refreshToken = refreshTokenRepository.findByTokenAndUserAgentAndExpiresAtAfter(payload.getRefreshToken(), userAgent, Instant.now(), RefreshToken.class)
                    .orElseThrow(() -> new AuthenticationFailedException("Invalid credentials"));

            user = findById(refreshToken.getId());
        } else if (StringUtils.hasText(payload.getUsername()) && StringUtils.hasText(payload.getPassword())) {
            try {
                user = findByUsername(payload.getUsername());

                if (!passwordEncoder.matches(payload.getPassword(), user.getPassword())) {
                    throw new AuthenticationFailedException("Invalid credentials");
                }
            } catch (EntityNotFoundException ex) {
                throw new AuthenticationFailedException("Invalid credentials");
            }
        }

        if (user == null) {
            throw new AuthenticationFailedException("Invalid credentials");
        }

        final String token = jwtService.generateToken(user);
        final String renewedRefreshToken = renewRefreshToken(user, refreshToken, userAgent);

        return new AuthResponse(token, renewedRefreshToken);
    }


    private String renewRefreshToken(final User user, RefreshToken refreshToken, final String userAgent) {
        final String newRefreshTokenCode = jwtService.generateRefreshToken();

        if (refreshToken == null) {
            refreshToken = refreshTokenRepository.findByUserIdAndUserAgentAndExpiresAtAfter(user.getId(), userAgent, Instant.now(), RefreshToken.class)
                    .orElse(new RefreshToken());
        }

        refreshToken.setOldToken(refreshToken.getOldToken());
        refreshToken.setUser(user);
        refreshToken.setUserAgent(userAgent);
        refreshToken.setToken(newRefreshTokenCode);
        refreshToken.setExpiresAt(Instant.now().plusMillis(JwtService.REFRESH_TOKEN_VALIDITY));

        this.refreshTokenRepository.save(refreshToken);

        return newRefreshTokenCode;
    }

    public User update(long id, UpdateUserPayload updateUserPayload) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setRole(updateUserPayload.getRole());

            if (updateUserPayload.getPassword() != null) {
                existingUser.setPassword(passwordEncoder.encode(updateUserPayload.getPassword()));
            }

            existingUser.setEmail(updateUserPayload.getEmail());
            existingUser.setUsername(updateUserPayload.getUsername());
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public User create(AddUserPayload addUserPayload) {
        boolean existsByUsernameOrEmail = this.userRepository.existsByUsernameOrEmail(addUserPayload.getUsername(), addUserPayload.getEmail());

        if (existsByUsernameOrEmail) {
            throw new IllegalArgumentException("Username or email already exist!");
        }

        User newUser = new User();
        newUser.setPassword(passwordEncoder.encode(addUserPayload.getPassword()));
        newUser.setUsername(addUserPayload.getUsername());
        newUser.setEmail(addUserPayload.getEmail());
        newUser.setRole(addUserPayload.getRole());

        return userRepository.save(newUser);
    }

    public void delete(long id) {
        if (!userRepository.existsById(id)){
            throw new IllegalArgumentException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
    }

    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id [%d] does not exist.".formatted(id)));
    }

    public User findByUsername(String username) {
        return userRepository.findUserByUsername(username).orElseThrow(() -> new EntityNotFoundException("User with username: " + username + " was not found."));
    }
}
