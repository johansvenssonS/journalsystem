package com.example.journalsystem.service;

import com.example.journalsystem.dto.StaffEmploymentDto;

import com.example.journalsystem.mapper.StaffEmploymentMapper;
import com.example.journalsystem.repository.DepartmentRepository;
import com.example.journalsystem.repository.RoleRepository;
import com.example.journalsystem.repository.StaffEmploymentRepository;
import com.example.journalsystem.repository.StaffRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffEmploymentService {

    private final StaffEmploymentRepository staffEmploymentRepository;
    private final StaffRepository staffRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final StaffEmploymentMapper staffEmploymentMapper;

    public StaffEmploymentService(StaffEmploymentRepository staffEmploymentRepository,
                                  StaffRepository staffRepository,
                                  RoleRepository roleRepository,
                                  DepartmentRepository departmentRepository,
                                  StaffEmploymentMapper staffEmploymentMapper) {
        this.staffEmploymentRepository = staffEmploymentRepository;
        this.staffRepository = staffRepository;
        this.roleRepository = roleRepository;
        this.departmentRepository = departmentRepository;
        this.staffEmploymentMapper = staffEmploymentMapper;
    }

    // Get all employments
    public List<StaffEmploymentDto> getAll() {
        return staffEmploymentRepository.findAll().stream()
                .map(staffEmploymentMapper::toDto)
                .toList();
    }

}