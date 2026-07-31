package com.example.journalsystem.service;

import com.example.journalsystem.dto.EntityDTO;
import com.example.journalsystem.entities.Entity;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.mapper.EntityMapper;
import com.example.journalsystem.repository.EntityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntityService {

    private final EntityRepository entityRepository;
    private final EntityMapper entityMapper;

    public EntityService(EntityRepository entityRepository,
                         EntityMapper entityMapper) {
        this.entityRepository = entityRepository;
        this.entityMapper = entityMapper;
    }

    public List<EntityDTO> getAll() {
        return entityRepository.findAll().stream()
                .map(entityMapper::toDto)
                .toList();
    }

    public EntityDTO getEntityById(Long id) {
        Entity entity = entityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entitet med id: " + id + " hittades inte"));
        return entityMapper.toDto(entity);
    }
}