package com.example.journalsystem.mapper;

import com.example.journalsystem.dto.StaffDTO;
import com.example.journalsystem.entities.Staff;
import org.springframework.stereotype.Component;

@Component
public class StaffMapper {

    public StaffDTO toDto(Staff staff) {
        return new StaffDTO(
                staff.getId(),
                staff.getPersonalNumber(),
                staff.getFirstName(),
                staff.getLastName()
        );
    }

}
