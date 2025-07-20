package com.example.Blog.Project.security;

import com.example.Blog.Project.permission.model.PermissionOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests((requests) -> requests

                        // Available for all
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/files/**").permitAll()

                        // Categories
                        .requestMatchers(HttpMethod.GET, "/categories/**").hasAnyAuthority(PermissionOption.READ_CATEGORIES.getAbbreviation())
                        .requestMatchers(HttpMethod.POST, "/categories/**").hasAnyAuthority(PermissionOption.CREATE_CATEGORIES.getAbbreviation())
                        .requestMatchers(HttpMethod.PUT, "/categories/**").hasAnyAuthority(PermissionOption.UPDATE_CATEGORIES.getAbbreviation())

                        // Posts
                        .requestMatchers(HttpMethod.GET, "/posts/**").hasAnyAuthority(PermissionOption.READ_POSTS.getAbbreviation())
                        .requestMatchers(HttpMethod.PUT, "/posts/*/like").hasAnyAuthority(PermissionOption.READ_POSTS.getAbbreviation())
                        .requestMatchers(HttpMethod.GET, "/posts/**").hasAnyAuthority(PermissionOption.READ_POSTS.getAbbreviation())
                        .requestMatchers(HttpMethod.POST, "/posts/**").hasAnyAuthority(PermissionOption.CREATE_POSTS.getAbbreviation())
                        .requestMatchers(HttpMethod.PUT, "/posts/**").hasAnyAuthority(PermissionOption.UPDATE_POSTS.getAbbreviation())
                        .requestMatchers(HttpMethod.DELETE, "/posts/**").hasAnyAuthority(PermissionOption.DELETE_POSTS.getAbbreviation())

                        // Roles
                        .requestMatchers(HttpMethod.GET, "/roles/**").hasAnyAuthority(PermissionOption.READ_ROLES.getAbbreviation())
                        .requestMatchers(HttpMethod.POST, "/roles/**").hasAnyAuthority(PermissionOption.CREATE_ROLES.getAbbreviation())
                        .requestMatchers(HttpMethod.PUT, "/roles/**").hasAnyAuthority(PermissionOption.UPDATE_ROLES.getAbbreviation())
                        .requestMatchers(HttpMethod.DELETE, "/roles/**").hasAnyAuthority(PermissionOption.DELETE_ROLES.getAbbreviation())

                        // Users
                        .requestMatchers(HttpMethod.GET, "/users/**").hasAnyAuthority(PermissionOption.READ_USERS.getAbbreviation())
                        .requestMatchers(HttpMethod.POST, "/users/**").hasAnyAuthority(PermissionOption.CREATE_USERS.getAbbreviation())
                        .requestMatchers(HttpMethod.PUT, "/users/**").hasAnyAuthority(PermissionOption.UPDATE_USERS.getAbbreviation())
                        .requestMatchers(HttpMethod.DELETE, "/users/**").hasAnyAuthority(PermissionOption.DELETE_USERS.getAbbreviation())

                        // Files
                        .requestMatchers(HttpMethod.POST, "/files/**").hasAnyAuthority(PermissionOption.UPDATE_POSTS.getAbbreviation(), PermissionOption.CREATE_POSTS.getAbbreviation())

                        // Statistics
                        .requestMatchers(HttpMethod.GET, "/activity/**").hasAnyAuthority(PermissionOption.READ_STATISTICS.getAbbreviation())
                        .requestMatchers(HttpMethod.GET, "/deleted-files/**").hasAnyAuthority(PermissionOption.READ_STATISTICS.getAbbreviation())

                        .anyRequest().authenticated()
                )
                .sessionManagement(c -> {
                    c.sessionFixation().newSession();
                    c.maximumSessions(1);
                });

        http.addFilterAt(this.jwtFilter, LogoutFilter.class);

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));;
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
