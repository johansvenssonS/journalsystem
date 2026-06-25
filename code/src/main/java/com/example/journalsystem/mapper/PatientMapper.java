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
}
