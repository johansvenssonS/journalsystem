package com.example.journalsystem.service;


import com.example.journalsystem.dto.CreateDiagnosisRequest;
import com.example.journalsystem.dto.DiagnosisResponse;
import com.example.journalsystem.entities.*;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.repository.DiagnosisRepository;
import com.example.journalsystem.repository.JournalRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class DiagnosisService {
    public final DiagnosisRepository diagnosisRepository;
    public final DiagnosisMapper diagnosisMapper;
    public final JournalRepository journalRepository;


    public DiagnosisService(DiagnosisRepository diagnosisRepository, DiagnosisMapper diagnosisMapper,
                             JournalRepository journalRepository) {
        this.diagnosisRepository = diagnosisRepository;
        this.diagnosisMapper = diagnosisMapper;
        this.journalRepository = journalRepository;
    }

    public List<DiagnosisDTO> getAllDiagnosis(){
        return diagnosisRepository.findAll().stream()
                .map(diagnosisMapper::toDto)
                .toList();
    }

    public DiagnosisDTO getDiagnosisById(Long id){
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Vårdkontakt med id: "+ id + " hittades inte"));
        return diagnosisMapper.toDto(diagnosis);
    }

    public DiagnosisResponse createDiagnosis(CreateDiagnosisRequest createDiagnosisRequest){
        Long journalEntryId = createDiagnosisRequest.getJournalEntryId();

        if (!journalRepository.existsById(journalEntryId)) {
            throw new ResourceNotFoundException("Journalpost med id: " + journalEntryId + " hittades inte");
        }

        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setJournalEntryId(journalEntryId);
        diagnosis.setSetBy(createDiagnosisRequest.getSetBy());
        diagnosis.setIcd10Code(createDiagnosisRequest.getIcd10Code());
        diagnosis.setName(createDiagnosisRequest.getName());
        diagnosis.setDescription(createDiagnosisRequest.getDescription());
        diagnosis.setDiagnosedDate(createDiagnosisRequest.getDiagnosedDate() != null
                ? createDiagnosisRequest.getDiagnosedDate()
                : new Date(System.currentTimeMillis()));

        Diagnosis saved = diagnosisRepository.save(diagnosis);

        return new DiagnosisResponse(
                saved.getId(),
                saved.getJournalEntryId(),
                saved.getSetBy(),
                saved.getIcd10Code(),
                saved.getName(),
                saved.getDescription(),
                saved.getDiagnosedDate()
        );
    }

}
