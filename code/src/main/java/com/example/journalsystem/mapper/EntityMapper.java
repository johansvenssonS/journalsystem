package com.example.journalsystem.mapper;

import com.example.journalsystem.dto.EntityDTO;
import com.example.journalsystem.entities.Entity;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {

    public EntityDTO toDto(Entity entity) {
        if (entity == null) {
            return null;
        }

        return new EntityDTO(
                entity.getId(),
                entity.getEntity()
        );
    }
}