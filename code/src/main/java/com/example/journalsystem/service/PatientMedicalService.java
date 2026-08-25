package com.example.journalsystem.service;


import com.example.journalsystem.dto.PatientMedicalDto;
import com.example.journalsystem.entities.PatientMedical;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.mapper.PatientMedicalMapper;
import com.example.journalsystem.repository.PatientRepository;
import com.example.journalsystem.repository.PatientMedicalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public PatientMedicalDto getPatientMedicalById(Long id) {
        var pm = patientMedicalRepository.findById(id)
                .orElseThrow(() -> new com.example.journalsystem.exceptions.ResourceNotFoundException("Patient medical med Id: " + id + " hittades inte"));
        return patientMedicalMapper.toDto(pm);
    }

    public PatientMedicalDto update(Long id, PatientMedicalDto dto) {

        // Patients created before this row was guaranteed at creation time (or any other gap)
        // shouldn't be stuck unable to ever save medical info — create it on first save instead.
        var pm = patientMedicalRepository.findById(id)
                .orElseGet(() -> {
                    var patient = patientRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Patient med Id: " + id + " hittades inte"));
                    var newPm = new PatientMedical();
                    newPm.setPatient(patient);
                    return newPm;
                });

        pm.setAllergies(dto.getAllergies());
        pm.setBloodType(dto.getBloodType());

        var updated = patientMedicalRepository.save(pm);
        return patientMedicalMapper.toDto(updated);
    }

}
