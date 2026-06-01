package com.example.journalsystem.mapper;

import com.example.journalsystem.dto.DepartmentDto;
import com.example.journalsystem.entities.Department;
import com.example.journalsystem.entities.Specialization;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {


    public DepartmentDto toDto(Department department) {
        String specializationName = department.getSpecialization() != null
                ? department.getSpecialization().getName()
                : "Unknown";

        return new DepartmentDto(
                department.getId(),
                department.getName(),
                department.getFloor(),
                specializationName
        );
    }
}