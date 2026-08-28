package com.example.journalsystem.service;

import com.example.journalsystem.dto.StaffContactDto;
import com.example.journalsystem.dto.StaffEmploymentDto;

import com.example.journalsystem.entities.Staff;
import com.example.journalsystem.entities.StaffContact;
import com.example.journalsystem.entities.StaffEmployment;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
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


    public StaffEmploymentDto getStaffEmploymentById(Long id) {
        StaffEmployment staffEmployment = staffEmploymentRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Personalanställning med id: " + id + " hittads inte"));
        return staffEmploymentMapper.toDto(staffEmployment);
    }
}