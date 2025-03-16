package com.example.Blog.Project.web.controller;

import com.example.Blog.Project.role.model.Role;
import com.example.Blog.Project.role.service.RoleService;
import com.example.Blog.Project.user.model.User;
import com.example.Blog.Project.web.dto.AddRolePayload;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<Page<Role>> getAll(Pageable pageable) {
        return ResponseEntity.ok(roleService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getById(@PathVariable long id){
        return ResponseEntity.ok(roleService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Role> update(@PathVariable long id, @Valid @RequestBody Role role){
        return ResponseEntity.ok(roleService.update(id, role));
    }

    @PostMapping("/add")
    public ResponseEntity<Role> create(@Valid @RequestBody AddRolePayload addRolePayload){
        return ResponseEntity.ok(roleService.create(addRolePayload));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id){
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
