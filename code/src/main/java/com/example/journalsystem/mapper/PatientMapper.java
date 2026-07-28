package com.example.journalsystem.mapper;

import com.example.journalsystem.dto.PatientDto;
import com.example.journalsystem.entities.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public PatientDto toDto(Patient patient){
        return new PatientDto(
                patient.getId(),
                patient.getPersonalNumber(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getDeletedAt()
        );
    }

    public Patient toEntity(PatientDto patientDto) {
        if (patientDto == null) {
            return null;
        }

        Patient patient = new Patient();
        patient.setId(patientDto.getId());
        patient.setPersonalNumber(patientDto.getPersonalNumber());
        patient.setFirstName(patientDto.getFirstName());
        patient.setLastName(patientDto.getLastName());
        patient.setDeletedAt(patientDto.getDeletedAt());

        return patient;
    }
}
