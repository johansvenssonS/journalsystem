package com.example.journalsystem.entities;


import org.springframework.stereotype.Component;

@Component
public class DiagnosisMapper {
    public DiagnosisDTO toDto(Diagnosis diagnosis){
        return new DiagnosisDTO(
                diagnosis.getId(),
                diagnosis.getSetBy(),
                diagnosis.getName(),
                diagnosis.getDescription(),
                diagnosis.getDiagnosedDate()
        );
    }
}
