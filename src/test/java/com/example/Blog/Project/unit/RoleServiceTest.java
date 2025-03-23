package com.example.Blog.Project.unit;

import com.example.Blog.Project.role.model.Role;
import com.example.Blog.Project.role.repository.RoleRepository;
import com.example.Blog.Project.role.service.RoleService;
import com.example.Blog.Project.web.dto.AddRolePayload;
import com.example.Blog.Project.web.dto.UpdateRolePayload;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RoleServiceTest {

    @InjectMocks
    private RoleService roleService;

    @Mock
    private RoleRepository roleRepository;

    @Test
    public void testGetAll() {
        Pageable pageable = PageRequest.of(0, 10);

        Role role1 = new Role();
        role1.setName("Admin");

        Role role2 = new Role();
        role2.setName("User");

        List<Role> roles = Arrays.asList(role1, role2);
        Page<Role> rolePage = new PageImpl<>(roles, pageable, roles.size());

        when(roleRepository.findAll(pageable)).thenReturn(rolePage);

        Page<Role> result = roleService.getAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getContent().size());
        assertEquals("Admin", result.getContent().get(0).getName());
        assertEquals("User", result.getContent().get(1).getName());

        verify(roleRepository).findAll(pageable);
    }

    @Test
    public void testUpdateRole_Success() {
        long roleId = 1L;
        Role existingRole = new Role();
        existingRole.setId(roleId);
        existingRole.setName("CustomRole");

        UpdateRolePayload roleData = new UpdateRolePayload();
        roleData.setName("UpdatedRole");

        Role updatedRole = new Role();
        updatedRole.setId(roleId);
        updatedRole.setName("UpdatedRole");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));
        when(roleRepository.save(any(Role.class))).thenReturn(updatedRole);

        Role result = roleService.update(roleId, roleData);

        assertNotNull(result);
        assertEquals("UpdatedRole", result.getName());

        verify(roleRepository).findById(roleId);
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    public void testUpdateRole_AdminRole_ShouldThrowException() {
        long roleId = 1L;
        Role existingRole = new Role();
        existingRole.setId(roleId);
        existingRole.setName("Admin");

        UpdateRolePayload roleData = new UpdateRolePayload();
        roleData.setName("NewAdminRole");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> roleService.update(roleId, roleData));
        assertEquals("Admin and User role cannot be updated", exception.getMessage());

        verify(roleRepository).findById(roleId);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    public void testUpdateRole_UserRole_ShouldThrowException() {
        long roleId = 1L;
        Role existingRole = new Role();
        existingRole.setId(roleId);
        existingRole.setName("User");

        UpdateRolePayload roleData = new UpdateRolePayload();
        roleData.setName("NewUserRole");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> roleService.update(roleId, roleData));
        assertEquals("Admin and User role cannot be updated", exception.getMessage());

        verify(roleRepository).findById(roleId);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    public void testUpdateRole_NotFound_ShouldThrowException() {
        long roleId = 1L;
        UpdateRolePayload roleData = new UpdateRolePayload();
        roleData.setName("SomeRole");

        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> roleService.update(roleId, roleData));
        assertEquals("Role not found", exception.getMessage());

        verify(roleRepository).findById(roleId);
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    public void testCreateRole_Success() {

        AddRolePayload addRolePayload = new AddRolePayload();
        addRolePayload.setName("CustomRole");

        Role newRole = new Role();
        newRole.setName(addRolePayload.getName());
        newRole.setPermissions(addRolePayload.getPermissions());

        when(roleRepository.findByName(addRolePayload.getName())).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(newRole);

        Role createdRole = roleService.create(addRolePayload);

        assertNotNull(createdRole);
        assertEquals("CustomRole", createdRole.getName());

        verify(roleRepository).findByName(addRolePayload.getName());
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    public void testCreateRole_AlreadyExists_ShouldThrowException() {

        AddRolePayload addRolePayload = new AddRolePayload();
        addRolePayload.setName("ExistingRole");

        Role existingRole = new Role();
        existingRole.setName("ExistingRole");

        when(roleRepository.findByName(addRolePayload.getName())).thenReturn(Optional.of(existingRole));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> roleService.create(addRolePayload));
        assertEquals("Role Name already exist!", exception.getMessage());

        verify(roleRepository).findByName(addRolePayload.getName());
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    public void testDeleteRole_Success() {
        long roleId = 1L;
        Role existingRole = new Role();
        existingRole.setId(roleId);
        existingRole.setName("CustomRole");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));

        roleService.delete(roleId);

        verify(roleRepository).findById(roleId);
        verify(roleRepository).deleteById(roleId);
    }

    @Test
    public void testDeleteRole_AdminRole_ShouldThrowException() {
        long roleId = 1L;
        Role existingRole = new Role();
        existingRole.setId(roleId);
        existingRole.setName("Admin");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> roleService.delete(roleId));
        assertEquals("Admin and User role cannot be deleted", exception.getMessage());

        verify(roleRepository).findById(roleId);
        verify(roleRepository, never()).deleteById(roleId);
    }

    @Test
    public void testDeleteRole_UserRole_ShouldThrowException() {
        long roleId = 1L;
        Role existingRole = new Role();
        existingRole.setId(roleId);
        existingRole.setName("User");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> roleService.delete(roleId));
        assertEquals("Admin and User role cannot be deleted", exception.getMessage());

        verify(roleRepository).findById(roleId);
        verify(roleRepository, never()).deleteById(roleId);
    }

    @Test
    public void testDeleteRole_NotFound_ShouldThrowException() {
        long roleId = 1L;

        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> roleService.delete(roleId));
        assertEquals("Role not found with id: " + roleId, exception.getMessage());

        verify(roleRepository).findById(roleId);
        verify(roleRepository, never()).deleteById(roleId);
    }

    @Test
    public void testFindById_Success() {
        long roleId = 1L;
        Role existingRole = new Role();
        existingRole.setId(roleId);
        existingRole.setName("CustomRole");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(existingRole));

        Role role = roleService.findById(roleId);

        assertNotNull(role);
        assertEquals(roleId, role.getId());
        assertEquals("CustomRole", role.getName());

        verify(roleRepository).findById(roleId);
    }

    @Test
    public void testFindById_NotFound_ShouldThrowException() {
        long roleId = 1L;

        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> roleService.findById(roleId));
        assertEquals("Role with id [1] does not exist.", exception.getMessage());

        verify(roleRepository).findById(roleId);
    }
}