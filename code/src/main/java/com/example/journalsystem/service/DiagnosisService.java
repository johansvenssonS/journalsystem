package com.example.journalsystem.service;


import com.example.journalsystem.entities.*;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.repository.DiagnosisRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiagnosisService {
    public final DiagnosisRepository diagnosisRepository;
    public final DiagnosisMapper diagnosisMapper;


    public DiagnosisService(DiagnosisRepository diagnosisRepository, DiagnosisMapper diagnosisMapper) {
        this.diagnosisRepository = diagnosisRepository;
        this.diagnosisMapper = diagnosisMapper;
    }

    public List<DiagnosisDTO> getAllDiagnosis(){
        return diagnosisRepository.findAll().stream()
                .map(diagnosis -> new DiagnosisDTO(
                        diagnosis.getId(),
                        diagnosis.getSetBy(),
                        diagnosis.getName(),
                        diagnosis.getDescription(),
                        diagnosis.getDiagnosedDate()
                )).toList();
    }

    public DiagnosisDTO getDiagnosisById(Long id){
        Diagnosis diagnosis = diagnosisRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Vårdkontakt med id: "+ id + " hittades inte"));
        return diagnosisMapper.toDto(diagnosis);
    }

}
