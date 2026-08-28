package com.example.journalsystem.service;

import com.example.journalsystem.dto.PatientDetailResponse;
import com.example.journalsystem.dto.PatientResponse;
import com.example.journalsystem.dto.CreatePatientRequest;
import com.example.journalsystem.entities.Patient;
import com.example.journalsystem.entities.PatientContact;
import com.example.journalsystem.entities.PatientMedical;
import com.example.journalsystem.exceptions.DuplicateResourceException;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.repository.PatientContactRepository;
import com.example.journalsystem.repository.PatientMedicalRepository;
import com.example.journalsystem.repository.PatientRepository;
import com.example.journalsystem.mapper.PatientMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final PatientContactRepository patientContactRepository;
    private final PatientMedicalRepository patientMedicalRepository;
    private final PatientAccessService patientAccessService;

    public PatientService(PatientRepository patientRepository, PatientMapper patientMapper,
                          PatientContactRepository patientContactRepository,
                          PatientMedicalRepository patientMedicalRepository,
                          PatientAccessService patientAccessService) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
        this.patientContactRepository = patientContactRepository;
        this.patientMedicalRepository = patientMedicalRepository;
        this.patientAccessService = patientAccessService;
    }

    /// US-2 — vårdpersonal ser bara patienter som är inskrivna på deras
    /// egen avdelning. Receptionisten ser hela listan för registrering
    /// och bokning, men når ingen medicinsk data (US-42).
    @Transactional(readOnly = true)
    public List<PatientResponse> getAll(){
        if (!patientAccessService.isClinicalUser()) {
            return patientRepository.findAll().stream()
                    .map(patientMapper::toDto)
                    .toList();
        }
        List<Long> allowedIds = patientAccessService.accessiblePatientIds();
        if (allowedIds.isEmpty()) {
            return List.of();
        }
        return patientRepository.findAllById(allowedIds).stream()
                .map(patientMapper::toDto)
                .toList();
    }

    /// US-51 — nekas om patienten inte har en aktiv vårdkontakt på
    /// den inloggades avdelning.
    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long id) {
        patientAccessService.assertCanAccessPatient(id);
        var patient = patientRepository.findById(id)
                .orElseThrow(() -> new com.example.journalsystem.exceptions.ResourceNotFoundException("Patient med Id: " + id + " hittades inte"));
        return patientMapper.toDto(patient);
    }

    @Transactional
    public PatientResponse createPatient(CreatePatientRequest createPatientRequest) {
        var personalNumber = createPatientRequest.getPersonalNumber();
        if (patientRepository.existsByPersonalNumber(personalNumber)) {
            throw new DuplicateResourceException("En patient med personnumret " + personalNumber + " finns redan");
        }
        Patient patient = patientMapper.toEntity(createPatientRequest);
        Patient savedPatient = patientRepository.save(patient);

        // Every patient needs its 1:1 patient_contact and patient_medical companion rows to
        // exist from the start — vårdpersonal fyller i uppgifterna senare via respektive flik.
        PatientContact patientContact = new PatientContact();
        patientContact.setPatient(savedPatient);
        patientContactRepository.save(patientContact);

        PatientMedical patientMedical = new PatientMedical();
        patientMedical.setPatient(savedPatient);
        patientMedicalRepository.save(patientMedical);

        return patientMapper.toDto(savedPatient);
    }

    /// US-10 kombinerad med US-51 — sökningen är öppen för fler roller,
    /// men vårdpersonal får bara träff på patienter på sin egen avdelning.
    @Transactional(readOnly = true)
    public PatientResponse getPatientByPersonalNumber(String personalNumber) {
        var patient = patientRepository.findByPersonalNumber(personalNumber)
                .orElseThrow(() -> new com.example.journalsystem.exceptions.ResourceNotFoundException("Patient med Id: " + personalNumber + " hittades inte"));
        patientAccessService.assertCanAccessPatient(patient.getId());
        return patientMapper.toDto(patient);
    }

    /// US-51 — samma avdelningsspärr som getPatientById.
    @Transactional(readOnly = true)
    public PatientDetailResponse getPatientDetails(Long id){
        patientAccessService.assertCanAccessPatient(id);
        var patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient med Id: " + id + " hittades inte"));
        var patientContact = patientContactRepository.findById(id)
                .orElse(null);

        return patientMapper.toDetailDto(patient, patientContact);
    }
}
















