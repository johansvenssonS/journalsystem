package com.example.journalsystem.mapper;

import com.example.journalsystem.dto.PrescriptionDTO;
import com.example.journalsystem.entities.Prescription;
import org.springframework.stereotype.Component;

@Component
public class PrescriptionMapper {

    public PrescriptionDTO toDto(Prescription prescription) {
        if (prescription == null) {
            return null;
        }

        return new PrescriptionDTO(
                prescription.getId(),
                prescription.getPatient().getId(),
                prescription.getPrescribedBy().getId(),
                prescription.getMedication(),
                prescription.getDosage(),
                prescription.getIssuedDate(),
                prescription.getEndDate(),
                prescription.getActive()
        );
    }

    public Prescription toEntity(PrescriptionDTO dto) {
        if (dto == null) {
            return null;
        }

        Prescription prescription = new Prescription();
        prescription.setMedication(dto.getMedication());
        prescription.setDosage(dto.getDosage());
        prescription.setIssuedDate(dto.getIssuedDate());
        prescription.setEndDate(dto.getEndDate());
        prescription.setActive(dto.getActive());

        return prescription;
    }
}