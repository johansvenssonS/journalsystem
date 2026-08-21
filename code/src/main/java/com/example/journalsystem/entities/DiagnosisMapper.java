package com.example.journalsystem.entities;


import org.springframework.stereotype.Component;

@Component
public class DiagnosisMapper {
    public DiagnosisDTO toDto(Diagnosis diagnosis){
        return new DiagnosisDTO(
                diagnosis.getId(),
                diagnosis.getJournalEntryId(),
                diagnosis.getSetBy(),
                diagnosis.getIcd10Code(),
                diagnosis.getName(),
                diagnosis.getDescription(),
                diagnosis.getDiagnosedDate()
        );
    }
}
