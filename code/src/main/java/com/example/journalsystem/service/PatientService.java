package com.example.journalsystem.service;

import com.example.journalsystem.dto.PatientDto;
import com.example.journalsystem.entities.Patient;
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

    public PatientDto getPatientById(Long id) {
        var patient = patientRepository.findById(id)
                .orElseThrow(() -> new com.example.journalsystem.exceptions.ResourceNotFoundException("Patient med Id: " + id + " hittades inte"));
        return patientMapper.toDto(patient);
    }
    public PatientDto createPatient(PatientDto patientDto) {
//        if (productRepository.existsByName(dto.getName())) {
//            throw new DuplicateProductException("En produkt med det namnet finns redan");
//        }
        Patient patient = patientMapper.toEntity(patientDto);
        Patient savedPatient = patientRepository.save(patient);
        return patientMapper.toDto(savedPatient);
    }
}














