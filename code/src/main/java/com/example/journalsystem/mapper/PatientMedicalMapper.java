package com.example.journalsystem.mapper;

import com.example.journalsystem.dto.PatientMedicalDto;
import com.example.journalsystem.entities.PatientMedical;
import org.springframework.stereotype.Component;

@Component
public class PatientMedicalMapper {

    public PatientMedicalDto toDto(PatientMedical patientMedical) {
      return new PatientMedicalDto(
        patientMedical.getId(),
        patientMedical.getAllergies(),
        patientMedical.getBloodType(),
        patientMedical.getCreatedAt()

      );
    }

/*    public PatientMedical toEntity(PatientMedicalDto patientMedicalDto) {
        PatientMedical patientMedical = new PatientMedical();
        patientMedical.setAllergies(dto.getAllergies());
        patientMedical.getBloodType(dto.getBloodType());
        patientMedical.setCreatedAt(dto.getCreatedAt());
        return patientMedical;
    }*/




}
