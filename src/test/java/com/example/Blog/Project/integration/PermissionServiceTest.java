package com.example.Blog.Project.integration;


import com.example.Blog.Project.permission.model.Permission;
import com.example.Blog.Project.permission.repository.PermissionRepository;
import com.example.Blog.Project.permission.service.PermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class PermissionServiceTest extends BaseTest{

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private PermissionRepository permissionRepository;

    @BeforeEach
    public void setup() {
        permissionRepository.deleteAll();
    }

    @Test
    public void testGetAll_ShouldReturnEmpty_WhenNoPermissionsExist() {
        List<Permission> permissions = permissionService.getAll();
        assertThat(permissions).isEmpty();
    }

    @Test
    public void testGetAll_ShouldReturnPermissions_WhenPermissionsExist() {
        Permission permission1 = new Permission();
        permission1.setTitle("Read");
        permission1.setDescription("to read");
        permissionRepository.save(permission1);

        Permission permission2 = new Permission();
        permission2.setTitle("Write");
        permission2.setDescription("to write");
        permissionRepository.save(permission2);

        List<Permission> permissions = permissionService.getAll();

        assertThat(permissions).isNotEmpty();
        assertThat(permissions).hasSize(2);
        assertThat(permissions.get(0).getTitle()).isEqualTo("Read");
        assertThat(permissions.get(1).getTitle()).isEqualTo("Write");
    }
}
