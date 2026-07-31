package com.example.journalsystem.dto;

public class EntityDTO {
    private Long id;
    private String entity;

    public EntityDTO(Long id, String entity) {
        this.id = id;
        this.entity = entity;
    }

    public Long getId() {
        return id;
    }

    public String getEntity() {
        return entity;
    }
}