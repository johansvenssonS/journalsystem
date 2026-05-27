package com.example.journalsystem.service;

import com.example.journalsystem.dto.RoleDto;
import com.example.journalsystem.mapper.RoleMapper;
import com.example.journalsystem.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    public List<RoleDto> getAll() {
        return roleRepository.findAll().stream()
                .map(roleMapper::toDto)
                .toList();
    }
}
