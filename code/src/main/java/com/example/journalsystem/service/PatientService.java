package com.example.journalsystem.service;

import com.example.journalsystem.dto.PatientDto;
import com.example.journalsystem.repository.PatientRepository;
import com.example.journalsystem.mapper.PatientMapper;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientService(PatientRepository patientRepository, PatientMapper patientMapper) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    public List<PatientDto> getAll(){
        return patientRepository.findAll().stream()
                .map(patientMapper::toDto)
                .toList();
    }
}
