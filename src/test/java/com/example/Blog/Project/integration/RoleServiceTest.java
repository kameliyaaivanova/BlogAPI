package com.example.Blog.Project.integration;


import com.example.Blog.Project.permission.model.Permission;
import com.example.Blog.Project.permission.model.PermissionOption;
import com.example.Blog.Project.role.model.Role;
import com.example.Blog.Project.role.repository.RoleRepository;
import com.example.Blog.Project.role.service.RoleService;
import com.example.Blog.Project.user.model.User;
import com.example.Blog.Project.web.dto.AddRolePayload;
import com.example.Blog.Project.web.dto.UpdateRolePayload;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RoleServiceTest extends BaseTest {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testGetAll() {
        assertTrue(roleService.getAll(Pageable.ofSize(20)).getContent().isEmpty());
    }

    @Test
    void testGetAllRoles_ShouldReturnRoles_WhenRolesExist() {

        Role role1 = new Role();
        role1.setName("Manager");
        roleRepository.save(role1);

        Role role2 = new Role();
        role2.setName("Editor");
        roleRepository.save(role2);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Role> rolesPage = roleService.getAll(pageable);

        assertThat(rolesPage.getTotalElements()).isEqualTo(2);
        assertEquals("Manager", rolesPage.getContent().getFirst().getName());
        assertEquals("Editor", rolesPage.getContent().getLast().getName());
    }

    @Test
    void testGetAllRoles_ShouldReturnEmpty_WhenNoRolesExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Role> rolesPage = roleService.getAll(pageable);
        assertThat(rolesPage.getTotalElements()).isEqualTo(0);
    }

    @Test
    void testFindById_ShouldReturnRole_WhenRoleExists() {
        Role role = new Role();
        role.setName("Support");
        Role savedRole = roleRepository.save(role);

        Role foundRole = roleService.findById(savedRole.getId());

        assertThat(foundRole).isNotNull();
        assertThat(foundRole.getName()).isEqualTo("Support");
    }

    @Test
    void testFindById_ShouldThrowException_WhenRoleDoesNotExist() {
        long invalidId = 999L;
        assertThrows(EntityNotFoundException.class, () -> roleService.findById(invalidId));
    }

    @Test
    void testCreateRole_ShouldSaveRole_WhenValidPayloadProvided() {
        AddRolePayload addRolePayload = new AddRolePayload();
        addRolePayload.setName("New Role");

        Role createdRole = roleService.create(addRolePayload);

        assertThat(createdRole).isNotNull();
        assertThat(createdRole.getName()).isEqualTo("New Role");
        assertThat(roleRepository.existsById(createdRole.getId())).isTrue();
    }

    @Test
    void testCreateRole_ShouldThrowException_WhenRoleAlreadyExists() {
        Role role = new Role();
        role.setName("DuplicateRole");
        roleRepository.save(role);

        AddRolePayload addRolePayload = new AddRolePayload();
        addRolePayload.setName("DuplicateRole");

        assertThrows(IllegalArgumentException.class, () -> roleService.create(addRolePayload));
    }

    @Test
    void testUpdateRole_ShouldUpdateRole_WhenValidPayloadProvided() {
        Role role = new Role();
        role.setName("Old Role");
        Role savedRole = roleRepository.save(role);

        UpdateRolePayload updatePayload = new UpdateRolePayload();
        updatePayload.setName("Updated Role");

        Role updatedRole = roleService.update(savedRole.getId(), updatePayload);

        assertThat(updatedRole.getName()).isEqualTo("Updated Role");
    }

    @Test
    void testUpdateRole_ShouldThrowException_WhenRoleIsAdminOrUser() {
        Role adminRole = new Role();
        adminRole.setName("Admin");
        Role savedAdminRole = roleRepository.save(adminRole);

        UpdateRolePayload updatePayload = new UpdateRolePayload();
        updatePayload.setName("Modified Admin");

        assertThrows(IllegalArgumentException.class, () -> roleService.update(savedAdminRole.getId(), updatePayload));
    }

    @Test
    void testUpdateRole_ShouldThrowException_WhenRoleDoesNotExist() {
        long invalidId = 999L;
        UpdateRolePayload updatePayload = new UpdateRolePayload();
        updatePayload.setName("New Name");

        assertThrows(EntityNotFoundException.class, () -> roleService.update(invalidId, updatePayload));
    }

    @Test
    void testDeleteRole_ShouldDeleteRole_WhenValidRoleIdProvided() {
        Role role = new Role();
        role.setName("Temporary Role");
        Role savedRole = roleRepository.save(role);

        roleService.delete(savedRole.getId());

        assertThat(roleRepository.existsById(savedRole.getId())).isFalse();
    }

    @Test
    void testDeleteRole_ShouldThrowException_WhenRoleIsAdminOrUser() {
        Role adminRole = new Role();
        adminRole.setName("Admin");
        Role savedAdminRole = roleRepository.save(adminRole);

        assertThrows(IllegalArgumentException.class, () -> roleService.delete(savedAdminRole.getId()));
    }

    @Test
    void testDeleteRole_ShouldThrowException_WhenRoleDoesNotExist() {
        long invalidId = 999L;
        assertThrows(IllegalArgumentException.class, () -> roleService.delete(invalidId));
    }

}
