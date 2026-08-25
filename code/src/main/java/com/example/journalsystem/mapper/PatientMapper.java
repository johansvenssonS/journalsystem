package com.example.journalsystem.mapper;

import com.example.journalsystem.dto.PatientDetailResponse;
import com.example.journalsystem.dto.PatientResponse;
import com.example.journalsystem.dto.CreatePatientRequest;
import com.example.journalsystem.entities.Patient;
import com.example.journalsystem.entities.PatientContact;
import org.springframework.stereotype.Component;

import java.util.Optional;

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

    public PatientDetailResponse toDetailDto(Patient patient, PatientContact patientContact) {
        return new PatientDetailResponse(
                patient.getId(),
                patient.getPersonalNumber(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getDeletedAt(),
                patientContact != null ? patientContact.getPhone() : null,
                patientContact != null ? patientContact.getEmail() : null,
                patientContact != null ? patientContact.getAddress() : null,
                patientContact != null ? patientContact.getEmergencyName() : null,
                patientContact != null ? patientContact.getEmergencyPhone() : null
        );
    }
}
