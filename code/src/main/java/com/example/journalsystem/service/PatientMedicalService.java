package com.example.journalsystem.service;


import com.example.journalsystem.mapper.PatientMedicalMapper;
import com.example.journalsystem.repository.PatientRepository;
import com.example.journalsystem.repository.PatientMedicalRepository;
import org.springframework.stereotype.Service;

@Service
public class PatientMedicalService {
    private final PatientMedicalRepository patientMedicalRepository;
    private final PatientRepository patientRepository;
    private final PatientMedicalMapper patientMedicalMapper;

    public PatientMedicalService(PatientMedicalRepository patientMedicalRepository,
                                 PatientRepository patientRepository,
                                 PatientMedicalMapper patientMedicalMapper) {
        this.patientMedicalRepository = patientMedicalRepository;
        this.patientRepository = patientRepository;
        this.patientMedicalMapper = patientMedicalMapper;
    }

    public List<PatientMedicalDto> getAll(){
        return patientMedicalRepository.findAll().stream().map(patientMedicalMapper::toDto).toList();
    }

}
