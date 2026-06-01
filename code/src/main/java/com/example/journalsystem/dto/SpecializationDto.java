package com.example.journalsystem.dto;

public class SpecializationDto {

    private Long id;
    private String name;

    public SpecializationDto() {
    }

    public SpecializationDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
