package com.example.Blog.Project.api;

import com.example.Blog.Project.permission.model.Permission;
import com.example.Blog.Project.permission.service.PermissionService;
import com.example.Blog.Project.web.controller.PermissionController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PermissionController.class)
public class PermissionControllerApiTest extends BaseApiTest{

    @MockitoBean
    private PermissionService permissionService;

    @Test
    @WithMockUser(username = "admin", authorities = {"p.r"})
    public void testGetAllPermissions_ShouldReturnPermissions_WhenPermissionsExist() throws Exception {

        Permission permission1 = new Permission();
        permission1.setDescription("to create posts");
        permission1.setTitle("CREATE POST");

        Permission permission2 = new Permission();
        permission2.setDescription("to delete posts");
        permission2.setTitle("DELETE POST");

        List<Permission> permissions = List.of(permission1, permission2);
        when(permissionService.getAll()).thenReturn(permissions);

        mockMvc.perform(get("/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is(permission1.getTitle())))
                .andExpect(jsonPath("$[1].title", is(permission2.getTitle())));

        verify(permissionService, times(1)).getAll();
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"p.r"})
    public void testGetAllPermissions_ShouldReturnEmptyList_WhenNoPermissionsExist() throws Exception {
        when(permissionService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(permissionService, times(1)).getAll();
    }

    @Test
    public void testGetAllPermissions_UnauthorizedUser_ShouldReturn403() throws Exception {
        mockMvc.perform(get("/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }


//    @Test
//    @WithMockUser(username = "user", authorities = {"w.w"})
//    public void testGetAllPermissions_ShouldReturn403_WhenUserLacksPermission() throws Exception {
//        mockMvc.perform(get("/permissions")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isForbidden());
//    }
}
