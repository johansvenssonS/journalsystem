package com.example.journalsystem.mapper;

import com.example.journalsystem.dto.PatientContactDto;
import com.example.journalsystem.entities.PatientContact;
import org.springframework.stereotype.Component;

@Component
public class PatientContactMapper {

    public PatientContactDto toDto(PatientContact patientContact) {
        return new PatientContactDto(
                patientContact.getId(),
                patientContact.getPhone(),
                patientContact.getEmail(),
                patientContact.getAddress(),
                patientContact.getEmergencyName(),
                patientContact.getEmergencyPhone()
        );
    }

    public PatientContact toEntity(PatientContactDto dto) {
        PatientContact patientContact = new PatientContact();
        patientContact.setPhone(dto.getPhone());
        patientContact.setEmail(dto.getEmail());
        patientContact.setAddress(dto.getAddress());
        patientContact.setEmergencyName(dto.getEmergencyName());
        patientContact.setEmergencyPhone(dto.getEmergencyPhone());
        return patientContact;
    }
    public void updateEntityFromDto(PatientContactDto dto, PatientContact patientContact) {
        if (dto == null || patientContact == null) {
            return;
        }

        patientContact.setPhone(dto.getPhone());
        patientContact.setEmail(dto.getEmail());
        patientContact.setAddress(dto.getAddress());
        patientContact.setEmergencyName(dto.getEmergencyName());
        patientContact.setEmergencyPhone(dto.getEmergencyPhone());
        // Do NOT touch getId() so the primary key remains unchanged
    }


}
