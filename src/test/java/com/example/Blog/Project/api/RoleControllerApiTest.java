package com.example.Blog.Project.api;

import com.example.Blog.Project.category.model.Category;
import com.example.Blog.Project.permission.model.Permission;
import com.example.Blog.Project.role.model.Role;
import com.example.Blog.Project.role.service.RoleService;
import com.example.Blog.Project.user.model.User;
import com.example.Blog.Project.web.controller.RoleController;
import com.example.Blog.Project.web.dto.AddCategoryPayload;
import com.example.Blog.Project.web.dto.AddRolePayload;
import com.example.Blog.Project.web.dto.UpdateRolePayload;
import com.example.Blog.Project.web.dto.UpdateUserPayload;
import jakarta.persistence.EntityNotFoundException;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.empty;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@WebMvcTest(RoleController.class)
public class RoleControllerApiTest extends BaseApiTest{

    @MockitoBean
    private RoleService roleService;

    @Test
    @WithMockUser(username = "Admin", authorities = {"r.r"})
    public void testGetAllRoles_Success() throws Exception {

        MockHttpServletRequestBuilder request = get("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        Permission permission = new Permission();
        permission.setTitle("Read Role");
        permission.setDescription("to read role");

        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("Admin");
        role1.setPermissions(Set.of(permission));

        Role role2 = new Role();
        role2.setId(2L);
        role2.setName("Domain");
        role2.setPermissions(Set.of(permission));

        when(roleService.getAll(Mockito.any())).thenReturn(new PageImpl<>(List.of(role1,role2), PageRequest.of(0, 20), 2));

        mockMvc.perform(request)
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.content[0].name", is(role1.getName())))
                .andExpect(jsonPath("$.content[1].name", is(role2.getName())))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(1)));
    }

    @Test
    void testGetAllRolesWhenNotAuthenticated() throws Exception {
        MockHttpServletRequestBuilder request = get("/roles");

        mockMvc.perform(request)
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @WithMockUser(username = "dummy", authorities = {"c.r", "c.c"})
    void testGetAllRolesWhenAuthenticatedButNoPermissions() throws Exception {
        MockHttpServletRequestBuilder request = get("/roles");

        mockMvc.perform(request)
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
    }

    @Test
    @WithMockUser(username = "dummy", authorities = {"r.r", "r.c", "c.r"})
    void testGetAllRolesWhenAuthenticatedWithSufficientPermissionsButNoRoles() throws Exception {
        MockHttpServletRequestBuilder request = get("/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        when(roleService.getAll(Mockito.any())).thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.content", empty()))
                .andExpect(jsonPath("$.totalElements", is(0)))
                .andExpect(jsonPath("$.totalPages", is(0)));
    }

    @Test
    @WithMockUser(username = "dummy", authorities = {"r.r"})
    void testGetRoleById_ShouldReturnRole_WhenRoleExists() throws Exception {

        Permission permission = new Permission();
        permission.setTitle("Read Role");
        permission.setDescription("to read role");

        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("Admin");
        role1.setPermissions(Set.of(permission));

        when(roleService.findById(1L)).thenReturn(role1);

        mockMvc.perform(get("/roles/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Admin")));

        verify(roleService, times(1)).findById(1L);
    }

    @Test
    @WithMockUser(username = "dummy", authorities = {"r.r"})
    void testGetRoleById_ShouldReturnNotFound_WhenRoleDoesNotExist() throws Exception {
        long roleId = 999L;

        when(roleService.findById(roleId)).thenThrow(new EntityNotFoundException("Role with give id does not exists!"));

        mockMvc.perform(get("/roles/{id}", roleId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"r.c"})
    void testCreateRole_ShouldReturnCreatedRole_WhenValidDataProvided() throws Exception {

        Permission permission = new Permission();
        permission.setTitle("Create Role");
        permission.setDescription("to create roles");

        AddRolePayload validRole = new AddRolePayload();
        validRole.setName("Admin");
        validRole.setPermissions(Set.of(permission));

        Role createdRole = new Role();
        createdRole.setName("Admin");
        createdRole.setPermissions(Set.of(permission));

        when(roleService.create(any(AddRolePayload.class))).thenReturn(createdRole);

        mockMvc.perform(post("/roles/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRole)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Admin")));

        verify(roleService, times(1)).create(any(AddRolePayload.class));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"r.c"})
    void testCreateRole_ShouldReturnBadRequest_WhenNameIsEmpty() throws Exception {

        Permission permission = new Permission();
        permission.setTitle("Create Role");
        permission.setDescription("to create roles");

        AddRolePayload invalidRole = new AddRolePayload();
        invalidRole.setName("");
        invalidRole.setPermissions(Set.of(permission));


        mockMvc.perform(post("/roles/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRole)))
                .andExpect(status().isBadRequest());

        verify(roleService, never()).create(any(AddRolePayload.class));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"r.c"})
    void testCreateRole_ShouldReturnConflict_WhenNameAlreadyExists() throws Exception {

        Permission permission = new Permission();
        permission.setTitle("Create Role");
        permission.setDescription("to create roles");

        AddRolePayload rolePayload = new AddRolePayload();
        rolePayload.setName("Admin");
        rolePayload.setPermissions(Set.of(permission));

        when(roleService.create(any()))
                .thenThrow(new IllegalArgumentException("Role Name already exists!"));

        mockMvc.perform(post("/roles/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rolePayload)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"c.c"})
    void testCreateRole_ShouldReturnForbidden_WhenUserNotAuthorized() throws Exception {

        Permission permission = new Permission();
        permission.setTitle("Create Role");
        permission.setDescription("to create roles");

        AddRolePayload rolePayload = new AddRolePayload();
        rolePayload.setName("Admin");
        rolePayload.setPermissions(Set.of(permission));

        mockMvc.perform(post("/roles/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rolePayload)))
                .andExpect(status().isForbidden());

        verify(roleService, never()).create(any(AddRolePayload.class));
    }

    @Test
    void testCreateRole_ShouldReturnForbidden_WhenNotAuthenticated() throws Exception {

        Permission permission = new Permission();
        permission.setTitle("Create Role");
        permission.setDescription("to create roles");

        AddRolePayload rolePayload = new AddRolePayload();
        rolePayload.setName("Admin");
        rolePayload.setPermissions(Set.of(permission));

        mockMvc.perform(post("/roles/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rolePayload)))
                .andExpect(status().isForbidden());

        verify(roleService, never()).create(any(AddRolePayload.class));
    }

    @Test
    @WithMockUser(username = "Admin", authorities = {"r.d"})
    public void testDeleteRole_Success() throws Exception {
        long roleId = 1L;

        doNothing().when(roleService).delete(roleId);

        mockMvc.perform(delete("/roles/{id}", roleId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(roleService, times(1)).delete(roleId);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"r.d"})
    void testDeleteRole_ShouldReturnNotFound_WhenRoleDoesNotExist() throws Exception {
        long roleId = 999L;

        doThrow(new EntityNotFoundException("Role not found")).when(roleService).delete(roleId);

        mockMvc.perform(delete("/roles/{id}", roleId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "Admin", authorities = {"r.d"})
    public void testDeleteRole_AdminRole_ShouldReturnConflict() throws Exception {

        Permission permission = new Permission();
        permission.setTitle("Create Role");
        permission.setDescription("to create roles");

        long roleId = 1L;
        Role adminRole = new Role();
        adminRole.setId(roleId);
        adminRole.setName("Admin");
        adminRole.setPermissions(Set.of(permission));


        when(roleService.findById(roleId)).thenReturn(adminRole);
        doThrow(new IllegalArgumentException("Admin and User role cannot be deleted"))
                .when(roleService).delete(roleId);

        mockMvc.perform(delete("/roles/{id}", roleId))
                .andExpect(status().isConflict());

        verify(roleService, times(1)).delete(roleId);
    }

    @Test
    @WithMockUser(username = "Admin", authorities = {"r.d"})
    public void testDeleteRole_UserRole_ShouldReturnConflict() throws Exception {

        Permission permission = new Permission();
        permission.setTitle("Create Role");
        permission.setDescription("to create roles");

        long roleId = 2L;
        Role userRole = new Role();
        userRole.setId(roleId);
        userRole.setName("User");

        when(roleService.findById(roleId)).thenReturn(userRole);
        doThrow(new IllegalArgumentException("Admin and User role cannot be deleted"))
                .when(roleService).delete(roleId);

        mockMvc.perform(delete("/roles/{id}", roleId))
                .andExpect(status().isConflict());

        verify(roleService, times(1)).delete(roleId);
    }

    @Test
    @WithMockUser(username = "User", authorities = {"r.r"})
    public void testDeleteRole_ShouldThrowForbiddenWhenNoPermissions() throws Exception {
        long roleId = 1L;

        mockMvc.perform(delete("/roles/{id}", roleId))
                .andExpect(status().isForbidden());

        verify(roleService, times(0)).delete(roleId);
    }

    @Test
    @WithMockUser(username = "Admin", authorities = {"r.u"})
    void testUpdateRole_ShouldReturnUpdatedRole_WhenRoleExists() throws Exception {
        long roleId = 1L;

        Permission permission = new Permission();
        permission.setTitle("Create Role");
        permission.setDescription("to create roles");

        UpdateRolePayload updateRolePayload = new UpdateRolePayload();
        updateRolePayload.setName("Domain");
        updateRolePayload.setPermissions(Set.of(permission));

        Role updatedRole = new Role();
        updatedRole.setName(updateRolePayload.getName());
        updatedRole.setPermissions(Set.of(permission));

        when(roleService.update(eq(roleId),any(UpdateRolePayload.class))).thenReturn(updatedRole);

        mockMvc.perform(put("/roles/{id}", roleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRolePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Domain")));

        verify(roleService, times(1)).update(eq(roleId), any(UpdateRolePayload.class));
    }

    @Test
    @WithMockUser(username = "Admin", authorities = {"r.u"})
    void testUpdateRole_ShouldReturnNotFoundException_WhenRoleDoesNotExist() throws Exception {
        long roleId = 999L;

        Permission permission = new Permission();
        permission.setTitle("Create Role");
        permission.setDescription("to create roles");

        UpdateRolePayload updateRolePayload = new UpdateRolePayload();
        updateRolePayload.setName("Domain");
        updateRolePayload.setPermissions(Set.of(permission));

        when(roleService.update(eq(roleId), any(UpdateRolePayload.class)))
                .thenThrow(new EntityNotFoundException("Role not found"));

        mockMvc.perform(put("/roles/{id}", roleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRolePayload)))
                .andExpect(status().isNotFound());

        verify(roleService, times(1)).update(eq(roleId), any(UpdateRolePayload.class));
    }

    @Test
    @WithMockUser(username = "Admin", authorities = {"r.u"})
    public void testUpdateRole_AdminRole_ShouldReturnConflict() throws Exception {
        long roleId = 1L;

        Permission permission = new Permission();
        permission.setTitle("Create Role");
        permission.setDescription("to create roles");

        UpdateRolePayload updateRolePayload = new UpdateRolePayload();
        updateRolePayload.setName("SuperAdmin");
        updateRolePayload.setPermissions(Set.of(permission));

        doThrow(new IllegalArgumentException("Admin and User role cannot be updated"))
                .when(roleService).update(eq(roleId), any(UpdateRolePayload.class));

        mockMvc.perform(put("/roles/{id}", roleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRolePayload)))
                .andExpect(status().isConflict());

        verify(roleService, times(1)).update(eq(roleId), any(UpdateRolePayload.class));
    }

    @Test
    @WithMockUser(username = "Admin", authorities = {"r.u"})
    public void testUpdateRole_UserRole_ShouldReturnConflict() throws Exception {
        long roleId = 2L;

        Permission permission = new Permission();
        permission.setTitle("Create Role");
        permission.setDescription("to create roles");

        UpdateRolePayload updateRolePayload = new UpdateRolePayload();
        updateRolePayload.setName("SuperUser");
        updateRolePayload.setPermissions(Set.of(permission));

        doThrow(new IllegalArgumentException("Admin and User role cannot be updated"))
                .when(roleService).update(eq(roleId), any(UpdateRolePayload.class));

        mockMvc.perform(put("/roles/{id}", roleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRolePayload)))
                .andExpect(status().isConflict());

        verify(roleService, times(1)).update(eq(roleId), any(UpdateRolePayload.class));
    }

    @Test
    @WithMockUser(username = "Admin", authorities = {"r.u"})
    public void testUpdateRole_ShouldReturnBadRequest_WhenInvalidDataIsProvided() throws Exception {
        long roleId = 1L;

        Permission permission = new Permission();
        permission.setTitle("Create Role");
        permission.setDescription("to create roles");

        UpdateRolePayload invalidPayload = new UpdateRolePayload();
        invalidPayload.setName("");
        invalidPayload.setPermissions(Set.of(permission));

        mockMvc.perform(put("/roles/{id}", roleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPayload)))
                .andExpect(status().isBadRequest());

        verify(roleService, never()).update(anyLong(), any(UpdateRolePayload.class));
    }

    @Test
    @WithMockUser(username = "User", authorities = {"r.r"})
    void testUpdateRole_ShouldReturnForbidden_WhenUserLacksPermissions() throws Exception {
        long roleId = 1L;

        Permission permission = new Permission();
        permission.setTitle("Create Role");
        permission.setDescription("to create roles");

        UpdateRolePayload updateRolePayload = new UpdateRolePayload();
        updateRolePayload.setName("UpdatedRole");
        updateRolePayload.setPermissions(Set.of(permission));

        mockMvc.perform(put("/roles/{id}", roleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRolePayload)))
                .andExpect(status().isForbidden());

        verify(roleService, never()).update(anyLong(), any(UpdateRolePayload.class));
    }
}
