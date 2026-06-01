// ========== MAPPER ==========
package com.example.journalsystem.mapper;

import com.example.journalsystem.dto.StaffContactDto;
import com.example.journalsystem.entities.StaffContact;
import org.springframework.stereotype.Component;

@Component
public class StaffContactMapper {

    public StaffContactDto toDto(StaffContact staffContact) {
        return new StaffContactDto(
                staffContact.getId(),
                staffContact.getEmail(),
                staffContact.getPhone(),
                staffContact.getAddress()
        );
    }

    public StaffContact toEntity(StaffContactDto dto) {
        StaffContact staffContact = new StaffContact();
        staffContact.setEmail(dto.getEmail());
        staffContact.setPhone(dto.getPhone());
        staffContact.setAddress(dto.getAddress());
        return staffContact;
    }
}