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

/*    public PatientContact toEntity(PatientContactDto dto) {
        PatientContact patientContact = new PatientContact();
        patientContact.setPhone(dto.getPhone());
        patientContact.getEmail(dto.getEmail());
        patientContact.getAddress(dto.getAddress());
        patientContact.getEmergencyName(dto.getEmergencyName());
        patientContact.getEmergencyPhone(dto.getEmergencyPhone());
        return patientContact;
    }*/


}
