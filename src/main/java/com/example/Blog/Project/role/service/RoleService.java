package com.example.Blog.Project.role.service;

import com.example.Blog.Project.category.model.Category;
import com.example.Blog.Project.role.model.Role;
import com.example.Blog.Project.role.repository.RoleRepository;
import com.example.Blog.Project.web.dto.AddRolePayload;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Page<Role> getAll(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }

    public Role update(long id, Role roleData) {
        Role existingRole = roleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Role not found"));

        String roleName = existingRole.getName();
        if (roleName.equalsIgnoreCase("Admin") || roleName.equalsIgnoreCase("User")) {
            throw new IllegalArgumentException("Admin and User role cannot be updated");
        }

        existingRole.setName(roleData.getName());
        existingRole.setPermissions(roleData.getPermissions());

        return roleRepository.save(existingRole);
    }

    public Role create(AddRolePayload addRolePayload) {
        Optional<Role> byName = this.roleRepository.findByName(addRolePayload.getName());

        if (byName.isPresent()){
            throw new IllegalArgumentException("Role Name already exist!");
        }

        Role newRole = new Role();
        newRole.setName(addRolePayload.getName());
        newRole.setPermissions(addRolePayload.getPermissions());

        return roleRepository.save(newRole);
    }

    public void delete(long id) {
        Role existingRole = roleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + id));

        if (existingRole.getName().equalsIgnoreCase("Admin") || existingRole.getName().equalsIgnoreCase("User")) {
            throw new IllegalArgumentException("Admin and User role cannot be deleted");
        }

        roleRepository.deleteById(id);
    }

    public Role findById(long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role with id [%d] does not exist.".formatted(id)));
    }
}
