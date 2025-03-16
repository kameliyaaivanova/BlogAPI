package com.example.Blog.Project.integration;

import com.example.Blog.Project.category.model.Category;
import com.example.Blog.Project.listeners.DataRemovalListener;
import com.example.Blog.Project.permission.model.Permission;
import com.example.Blog.Project.permission.repository.PermissionRepository;
import com.example.Blog.Project.post.model.Post;
import com.example.Blog.Project.role.model.Role;
import com.example.Blog.Project.role.repository.RoleRepository;
import com.example.Blog.Project.role.service.RoleService;
import com.example.Blog.Project.web.controller.PostController;
import com.example.Blog.Project.web.controller.RoleController;
import com.example.Blog.Project.web.dto.AddRolePayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@TestExecutionListeners(
        listeners = DataRemovalListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleController roleController;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void testGetRolesWhenNoAuthenticationIsPresent() {
        try {
            this.mockMvc.perform(get("/roles").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @WithMockUser(username = "dummy", authorities = { "c.c", "c.r" })
    public void testGetRolesWhenAuthenticationIsPresentButHaveNoPermissions() {
        try {
            this.mockMvc.perform(get("/roles").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @WithMockUser(username = "dummy", authorities = { "r.r" })
    public void testGetRolesWithPermissions() {
        try {
            this.mockMvc.perform(get("/roles").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content", empty()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @WithMockUser(username = "dummy", authorities = { "r.r" })
    public void testGetRolesWithPermissionsWhenThereAreRoles() {
        try {
            Permission p1 = new Permission();
            p1.setTitle("r.r");
            p1.setDescription("Read Roles");

            Permission p2 = new Permission();
            p2.setTitle("c.r");
            p2.setDescription("Create Roles");

            p1 = permissionRepository.save(p1);
            p2 = permissionRepository.save(p2);

            Role role = new Role();
            role.setName("Admin");
            role.setPermissions(Set.of(p1, p2));

            roleRepository.save(role);

            this.mockMvc.perform(get("/roles").contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].length()", is(4)))
                    .andExpect(jsonPath("$.content.length()", is(1)))
                    .andExpect(jsonPath("$.content[0].id", notNullValue()))
                    .andExpect(jsonPath("$.content[0].name", is("Admin")))
                    .andExpect(jsonPath("$.content[0].createdAt", notNullValue()))
                    .andExpect(jsonPath("$.content[0].permissions[0].length()", is(4)))
                    .andExpect(jsonPath("$.content[0].permissions.length()", is(2)))
                    .andExpect(jsonPath("$.content[0].permissions[0].id", notNullValue()))
                    .andExpect(jsonPath("$.content[0].permissions[0].title", is("r.r")))
                    .andExpect(jsonPath("$.content[0].permissions[0].description", is("Read Roles")))
                    .andExpect(jsonPath("$.content[0].permissions[0].createdAt", notNullValue()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @WithMockUser(username = "dummy", authorities = { "r.u", "r.d", "r.r" })
    public void testCreateRoleWithoutRoleCreatePermission() {
        try {
            this.mockMvc.perform(post("/roles/add").contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateRoleWithoutAuthentication() {
        try {
            this.mockMvc.perform(post("/roles/add").contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @WithMockUser(username = "dummy", authorities = { "r.c" })
    public void testCreateRoleWithoutPayload() {
        try {
            this.mockMvc.perform(post("/roles/add").contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    @Test
//    @WithMockUser(username = "dummy", authorities = { "r.c" })
//    public void testCreateRole() throws JsonProcessingException {
//
//        Role rolePayload = new Role();
//        rolePayload.setName("role1");
//
//        Permission p1 = new Permission();
//        p1.setTitle("r.r");
//        p1.setDescription("Read Roles");
//
//        Permission p2 = new Permission();
//        p2.setTitle("r.u");
//        p2.setDescription("Update Roles");
//
//        p1 = permissionRepository.save(p1);
//        p2 = permissionRepository.save(p2);
//
//        rolePayload.setPermissions(Set.of(p1,p2));
//
//        String jsonData = objectMapper.writeValueAsString(rolePayload);
//
//        try {
//            this.mockMvc.perform(post("/roles/add").content(jsonData).contentType(MediaType.APPLICATION_JSON))
//                    .andExpect(status().isOk())
//                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//                    .andExpect(jsonPath("$.name", is(rolePayload.getName())))
//                    .andExpect(jsonPath("$.permissions", is(rolePayload.getPermissions())))
//                    .andExpect(jsonPath("$.createdAt", notNullValue()));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }


//    @Test
//    public void testGetById_Success() throws Exception {
//
//        long roleId = 1L;
//        Role role = new Role();
//        role.setId(roleId);
//        role.setName("Admin");
//
//        when(roleService.findById(roleId)).thenReturn(role);
//
//        mockMvc.perform(get("/roles/{id}", roleId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(roleId))
//                .andExpect(jsonPath("$.name").value("Admin"));
//
//        verify(roleService, times(1)).findById(roleId);
//    }
//
//    @Test
//    public void testGetById_NotFound() throws Exception {
//        long roleId = 99L;
//
//        when(roleService.findById(roleId)).thenThrow(new EntityNotFoundException("Role not found"));
//
//        mockMvc.perform(get("/roles/{id}", roleId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isNotFound());
//
//        verify(roleService, times(1)).findById(roleId);
//    }



}
