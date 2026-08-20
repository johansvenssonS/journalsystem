package com.example.journalsystem.service;

import com.example.journalsystem.dto.PatientContactDto;
import com.example.journalsystem.entities.PatientContact;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
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

    public PatientContactDto getPatientContactById(Long id) {
        var contact = patientContactRepository.findById(id)
                .orElseThrow(() -> new com.example.journalsystem.exceptions.ResourceNotFoundException("Patient contact med Id: " + id + " hittades inte"));
        return patientContactMapper.toDto(contact);
    }

    public PatientContactDto update(Long id, PatientContactDto dto) {

        var contact = patientContactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PatientContact med Id: " + id + " hittades inte"));

        patientContactMapper.updateEntityFromDto(dto, contact);

        var updatedContact = patientContactRepository.save(contact);
        return patientContactMapper.toDto(updatedContact);


    }
}
