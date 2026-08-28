package com.example.journalsystem.mapper;

import com.example.journalsystem.dto.RoleDto;
import com.example.journalsystem.entities.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public RoleDto toDto(Role role) {

        return new RoleDto(
                role.getId(),
                role.getTitle()
        );
    }


}