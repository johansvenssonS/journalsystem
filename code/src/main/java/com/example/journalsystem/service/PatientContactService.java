package com.example.journalsystem.service;

import com.example.journalsystem.dto.PatientContactDto;
import com.example.journalsystem.mapper.PatientContactMapper;
import com.example.journalsystem.repository.PatientContactRepository;
import com.example.journalsystem.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientContactService {

    private final PatientContactRepository patientContactRepository;
    private final PatientRepository patientRepository;
    private final PatientContactMapper patientContactMapper;

    public PatientContactService(PatientContactRepository patientContactRepository,
                                 PatientRepository patientRepository,
                                 PatientContactMapper patientContactMapper) {
        this.patientContactRepository = patientContactRepository;
        this.patientRepository = patientRepository;
        this.patientContactMapper = patientContactMapper;
    }

    public List<PatientContactDto> getAll() {
        return patientContactRepository.findAll().stream().map(patientContactMapper::toDto).toList();
    }

}
