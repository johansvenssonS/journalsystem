package com.example.journalsystem.service;

import com.example.journalsystem.dto.CreatePrescriptionRequest;
import com.example.journalsystem.dto.PrescriptionDTO;
import com.example.journalsystem.dto.PrescriptionResponse;
import com.example.journalsystem.entities.Patient;
import com.example.journalsystem.entities.Prescription;
import com.example.journalsystem.entities.Staff;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.mapper.PrescriptionMapper;
import com.example.journalsystem.repository.PatientRepository;
import com.example.journalsystem.repository.PrescriptionRepository;
import com.example.journalsystem.repository.StaffRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionMapper prescriptionMapper;
    private final PatientRepository patientRepository;
    private final StaffRepository staffRepository;

    public PrescriptionService(PrescriptionRepository prescriptionRepository,
                               PrescriptionMapper prescriptionMapper,
                               PatientRepository patientRepository,
                               StaffRepository staffRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionMapper = prescriptionMapper;
        this.patientRepository = patientRepository;
        this.staffRepository = staffRepository;
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
    /// US-61 — apotekssystemet ska bara se just nu aktiva recept, inte hela receptheistoriken.
    public List<PrescriptionDTO> getPrescriptionByPersonalNumber(String personalNumber) {
        if (!patientRepository.existsByPersonalNumber(personalNumber)){
            throw new ResourceNotFoundException("Person med personnummer:" + personalNumber + " hittades inte");
        }
        return prescriptionRepository.findByPatient_PersonalNumber(personalNumber).stream()
                .filter(Prescription::getActive)
                .map(prescriptionMapper::toDto)
                .toList();
    }

    public PrescriptionResponse createPrescription(CreatePrescriptionRequest createPrescriptionRequest) {
        Long patientId = createPrescriptionRequest.getPatientId();
        Long prescribedById = createPrescriptionRequest.getPrescribedBy();

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient med id: " + patientId + " hittades inte"));

        Staff prescribedBy = staffRepository.findById(prescribedById)
                .orElseThrow(() -> new ResourceNotFoundException("Personal med id: " + prescribedById + " hittades inte"));

        Prescription prescription = new Prescription();
        prescription.setPatient(patient);
        prescription.setPrescribedBy(prescribedBy);
        prescription.setMedication(createPrescriptionRequest.getMedication());
        prescription.setDosage(createPrescriptionRequest.getDosage());
        prescription.setIssuedDate(createPrescriptionRequest.getIssuedDate() != null
                ? createPrescriptionRequest.getIssuedDate()
                : LocalDate.now());
        prescription.setEndDate(createPrescriptionRequest.getEndDate());
        prescription.setActive(createPrescriptionRequest.getActive() != null
                ? createPrescriptionRequest.getActive()
                : true);

        Prescription saved = prescriptionRepository.save(prescription);

        return new PrescriptionResponse(
                saved.getId(),
                saved.getPatient().getId(),
                saved.getPrescribedBy().getId(),
                saved.getMedication(),
                saved.getDosage(),
                saved.getIssuedDate(),
                saved.getEndDate(),
                saved.getActive()
        );
    }
}