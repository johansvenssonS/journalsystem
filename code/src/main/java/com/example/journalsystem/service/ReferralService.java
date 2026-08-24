package com.example.journalsystem.service;

import com.example.journalsystem.dto.CreateReferralRequest;
import com.example.journalsystem.dto.ReferralDTO;
import com.example.journalsystem.dto.ReferralResponse;
import com.example.journalsystem.entities.Department;
import com.example.journalsystem.entities.Patient;
import com.example.journalsystem.entities.Referral;
import com.example.journalsystem.entities.Staff;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.mapper.ReferralMapper;
import com.example.journalsystem.repository.DepartmentRepository;
import com.example.journalsystem.repository.PatientRepository;
import com.example.journalsystem.repository.ReferralRepository;
import com.example.journalsystem.repository.StaffRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ReferralService {

    private final ReferralRepository referralRepository;
    private final ReferralMapper referralMapper;
    private final PatientRepository patientRepository;
    private final DepartmentRepository departmentRepository;
    private final StaffRepository staffRepository;

    public ReferralService(ReferralRepository referralRepository,
                           ReferralMapper referralMapper,
                           PatientRepository patientRepository,
                           DepartmentRepository departmentRepository,
                           StaffRepository staffRepository) {
        this.referralRepository = referralRepository;
        this.referralMapper = referralMapper;
        this.patientRepository = patientRepository;
        this.departmentRepository = departmentRepository;
        this.staffRepository = staffRepository;
    }

    public List<ReferralDTO> getAll() {
        return referralRepository.findAll().stream()
                .map(referralMapper::toDto)
                .toList();
    }

    public ReferralDTO getReferralById(Long id) {
        Referral referral = referralRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Remiss med id: " + id + " hittades inte"));
        return referralMapper.toDto(referral);
    }

    public ReferralResponse createReferral(CreateReferralRequest createReferralRequest) {
        Long patientId = createReferralRequest.getPatientId();
        Long fromDepartmentId = createReferralRequest.getFromDepartmentId();
        Long toDepartmentId = createReferralRequest.getToDepartmentId();
        Long sentById = createReferralRequest.getSentBy();

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient med id: " + patientId + " hittades inte"));

        Department fromDepartment = departmentRepository.findById(fromDepartmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Avdelning med id: " + fromDepartmentId + " hittades inte"));

        Department toDepartment = departmentRepository.findById(toDepartmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Avdelning med id: " + toDepartmentId + " hittades inte"));

        Staff sentBy = staffRepository.findById(sentById)
                .orElseThrow(() -> new ResourceNotFoundException("Personal med id: " + sentById + " hittades inte"));

        Referral referral = new Referral();
        referral.setPatient(patient);
        referral.setFromDepartment(fromDepartment);
        referral.setToDepartment(toDepartment);
        referral.setSentBy(sentBy);
        referral.setReason(createReferralRequest.getReason());
        referral.setSentAt(createReferralRequest.getSentAt() != null
                ? createReferralRequest.getSentAt()
                : Instant.now());
        referral.setStatus("pending");

        Referral saved = referralRepository.save(referral);

        return new ReferralResponse(
                saved.getId(),
                saved.getPatient().getId(),
                saved.getFromDepartment().getId(),
                saved.getToDepartment().getId(),
                saved.getSentBy().getId(),
                saved.getReason(),
                saved.getSentAt(),
                saved.getStatus()
        );
    }
}