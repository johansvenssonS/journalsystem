package com.example.journalsystem.mapper;

import com.example.journalsystem.dto.StaffEmploymentDto;
import com.example.journalsystem.entities.StaffEmployment;
import org.springframework.stereotype.Component;

@Component
public class StaffEmploymentMapper {

    public StaffEmploymentDto toDto(StaffEmployment staffEmployment) {
        return new StaffEmploymentDto(
                staffEmployment.getId(),
                staffEmployment.getStaff().getId(),
                staffEmployment.getRole().getId(),
                staffEmployment.getDepartment().getId(),
                staffEmployment.getLicenseNo(),
                staffEmployment.getHiredDate()
        );
    }

    public StaffEmployment toEntity(StaffEmploymentDto dto) {
        StaffEmployment staffEmployment = new StaffEmployment();
        staffEmployment.setId(dto.getId());
        staffEmployment.setLicenseNo(dto.getLicenseNo());
        staffEmployment.setHiredDate(dto.getHiredDate());
        return staffEmployment;
    }
}