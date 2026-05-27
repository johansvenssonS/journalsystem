package com.example.journalsystem.service;

import com.example.journalsystem.dto.StaffDto;
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

    public List<StaffDto> getAll() {
        return staffRepository.findAll().stream()
                .map(staffMapper::toDto)
                .toList();
    }
}
