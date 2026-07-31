package com.example.journalsystem.service;

import com.example.journalsystem.dto.PrescriptionDTO;
import com.example.journalsystem.entities.Prescription;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.mapper.PrescriptionMapper;
import com.example.journalsystem.repository.PrescriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionMapper prescriptionMapper;

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                               PrescriptionMapper prescriptionMapper) {
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionMapper = prescriptionMapper;
    }

    public List<PrescriptionDTO> getAll() {
        return prescriptionRepository.findAll().stream()
                .map(prescriptionMapper::toDto)
                .toList();
    }

    public PrescriptionDTO getPrescriptionById(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recept med id: " + id + " hittades inte"));
        return prescriptionMapper.toDto(prescription);
    }
}