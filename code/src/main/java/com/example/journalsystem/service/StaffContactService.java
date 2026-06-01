// ========== SERVICE ==========
package com.example.journalsystem.service;

import com.example.journalsystem.dto.StaffContactDto;
import com.example.journalsystem.mapper.StaffContactMapper;
import com.example.journalsystem.repository.StaffContactRepository;
import com.example.journalsystem.repository.StaffRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffContactService {

    private final StaffContactRepository staffContactRepository;
    private final StaffRepository staffRepository;
    private final StaffContactMapper staffContactMapper;

    public StaffContactService(StaffContactRepository staffContactRepository,
                               StaffRepository staffRepository,
                               StaffContactMapper staffContactMapper) {
        this.staffContactRepository = staffContactRepository;
        this.staffRepository = staffRepository;
        this.staffContactMapper = staffContactMapper;
    }

    // Get all contacts
    public List<StaffContactDto> getAll() {
        return staffContactRepository.findAll().stream()
                .map(staffContactMapper::toDto)
                .toList();
    }
}
