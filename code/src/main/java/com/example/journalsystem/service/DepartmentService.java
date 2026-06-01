package com.example.journalsystem.service;

import com.example.journalsystem.dto.DepartmentDto;
import com.example.journalsystem.mapper.DepartmentMapper;
import com.example.journalsystem.repository.DepartmentRepository;
import com.example.journalsystem.repository.SpecializationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final SpecializationRepository specializationRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentService(DepartmentRepository departmentRepository, SpecializationRepository specializationRepository, DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.specializationRepository = specializationRepository;
        this.departmentMapper = departmentMapper;
    }

    public List<DepartmentDto> getAll() {
        return departmentRepository.findAll().stream()
                .map(departmentMapper::toDto)
                .toList();
    }
}
