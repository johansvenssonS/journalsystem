package com.example.journalsystem.dto;

public class DepartmentDto {

    private Long id;
    private String name;
    private Integer floor;
    private String specializationName;

    public DepartmentDto() {
    }

    public DepartmentDto(Long id, String name, Integer floor, String specializationName) {
        this.id = id;
        this.name = name;
        this.floor = floor;
        this.specializationName = specializationName;
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

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getSpecializationName() {
        return specializationName;
    }

    public void setSpecializationName(String specializationName) {
        this.specializationName = specializationName;
    }
}