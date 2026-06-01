package com.example.journalsystem.mapper;

import com.example.journalsystem.dto.StaffDto;
import com.example.journalsystem.entities.Staff;
import org.springframework.stereotype.Component;

@Component
public class StaffMapper {

    public StaffDto toDto(Staff staff) {
        return new StaffDto(
                staff.getId(),
                staff.getPersonalNumber(),
                staff.getFirstName(),
                staff.getLastName()
        );
    }

}
