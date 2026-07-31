package com.example.journalsystem.controller;

import com.example.journalsystem.dto.EntityDTO;
import com.example.journalsystem.service.EntityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/entities")
public class EntityController {

    private final EntityService entityService;

    public EntityController(EntityService entityService) {
        this.entityService = entityService;
    }

    @GetMapping
    public ResponseEntity<List<EntityDTO>> getAll() {
        return ResponseEntity.ok(entityService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(entityService.getEntityById(id));
    }
}