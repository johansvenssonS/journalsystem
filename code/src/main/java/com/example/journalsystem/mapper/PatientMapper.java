package com.example.journalsystem.mapper;

import com.example.journalsystem.dto.PatientResponse;
import com.example.journalsystem.dto.CreatePatientRequest;
import com.example.journalsystem.entities.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public PatientResponse toDto(Patient patient){
        return new PatientResponse(
                patient.getId(),
                patient.getPersonalNumber(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getDeletedAt()
        );
    }

    public Patient toEntity(CreatePatientRequest createPatientRequest) {
        if (createPatientRequest == null) {
            return null;
        }

        Patient patient = new Patient();
        patient.setPersonalNumber(createPatientRequest.getPersonalNumber());
        patient.setFirstName(createPatientRequest.getFirstName());
        patient.setLastName(createPatientRequest.getLastName());

        return patient;
    }
}
