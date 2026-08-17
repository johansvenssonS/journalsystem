package com.example.journalsystem.service;

import com.example.journalsystem.dto.StaffDTO;
import com.example.journalsystem.entities.Staff;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.mapper.StaffMapper;
import com.example.journalsystem.repository.StaffRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffService {

    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;

    public StaffService(StaffRepository staffRepository, StaffMapper staffMapper) {
        this.staffRepository = staffRepository;
        this.staffMapper = staffMapper;
    }

    public List<StaffDTO> getAll() {
        return staffRepository.findAll().stream()
                .map(staffMapper::toDto)
                .toList();
    }

    public StaffDTO getStaffById(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Personal med Id:" + id + " hittades inte"));
        return staffMapper.toDto(staff);
    }
    
}
