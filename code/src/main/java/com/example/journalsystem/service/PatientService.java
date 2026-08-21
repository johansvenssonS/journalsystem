package com.example.journalsystem.service;

import com.example.journalsystem.dto.PatientDetailResponse;
import com.example.journalsystem.dto.PatientResponse;
import com.example.journalsystem.dto.CreatePatientRequest;
import com.example.journalsystem.entities.Patient;
import com.example.journalsystem.entities.PatientContact;
import com.example.journalsystem.exceptions.DuplicateResourceException;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.repository.PatientContactRepository;
import com.example.journalsystem.repository.PatientRepository;
import com.example.journalsystem.mapper.PatientMapper;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final PatientContactRepository patientContactRepository;

    public PatientService(PatientRepository patientRepository, PatientMapper patientMapper, PatientContactRepository patientContactRepository) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
        this.patientContactRepository = patientContactRepository;
    }

    public List<PatientResponse> getAll(){
        return patientRepository.findAll().stream()
                .map(patientMapper::toDto)
                .toList();
    }

    public PatientResponse getPatientById(Long id) {
        var patient = patientRepository.findById(id)
                .orElseThrow(() -> new com.example.journalsystem.exceptions.ResourceNotFoundException("Patient med Id: " + id + " hittades inte"));
        return patientMapper.toDto(patient);
    }

    public PatientResponse createPatient(CreatePatientRequest createPatientRequest) {
        var personalNumber = createPatientRequest.getPersonalNumber();
        if (patientRepository.existsByPersonalNumber(personalNumber)) {
            throw new DuplicateResourceException("En patient med personnumret " + personalNumber + " finns redan");
        }
        Patient patient = patientMapper.toEntity(createPatientRequest);
        Patient savedPatient = patientRepository.save(patient);
        return patientMapper.toDto(savedPatient);
    }

    public PatientResponse getPatientByPersonalNumber(String personalNumber) {
        var patient = patientRepository.findByPersonalNumber(personalNumber)
                .orElseThrow(() -> new com.example.journalsystem.exceptions.ResourceNotFoundException("Patient med Id: " + personalNumber + " hittades inte"));
        return patientMapper.toDto(patient);
    }

    public PatientDetailResponse getPatientDetails(Long id){
        var patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient med Id: " + id + " hittades inte"));
        var patientContact = patientContactRepository.findById(id)
                .orElse(null);

        return patientMapper.toDetailDto(patient, patientContact);
    }
}
















