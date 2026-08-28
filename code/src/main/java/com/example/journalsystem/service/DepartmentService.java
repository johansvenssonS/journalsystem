package com.example.journalsystem.service;

import com.example.journalsystem.dto.DepartmentDto;
import com.example.journalsystem.dto.DepartmentPatientDTO;
import com.example.journalsystem.entities.CareContact;
import com.example.journalsystem.entities.Department;
import com.example.journalsystem.entities.Patient;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.mapper.DepartmentMapper;
import com.example.journalsystem.repository.CareContactRepository;
import com.example.journalsystem.repository.DepartmentRepository;
import com.example.journalsystem.repository.PatientRepository;
import com.example.journalsystem.repository.SpecializationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    /// US-20 — status i care_contact för en patient som just nu är inskriven.
    /// Samma värde som CareContactService.STATUS_ADMITTED i US-12; slå ihop
    /// dem till en gemensam konstant när båda brancherna är mergade.
    public static final String STATUS_ADMITTED = "admitted";

    private final DepartmentRepository departmentRepository;
    private final SpecializationRepository specializationRepository;
    private final DepartmentMapper departmentMapper;
    private final CareContactRepository careContactRepository;
    private final PatientRepository patientRepository;

    public DepartmentService(DepartmentRepository departmentRepository,
                             SpecializationRepository specializationRepository,
                             DepartmentMapper departmentMapper,
                             CareContactRepository careContactRepository,
                             PatientRepository patientRepository) {
        this.departmentRepository = departmentRepository;
        this.specializationRepository = specializationRepository;
        this.departmentMapper = departmentMapper;
        this.careContactRepository = careContactRepository;
        this.patientRepository = patientRepository;
    }

    public List<DepartmentDto> getAll() {
        return departmentRepository.findAll().stream()
                .map(departmentMapper::toDto)
                .toList();
    }

    public DepartmentDto getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Avdelning med id: " + id + "hittades inte"));
        return departmentMapper.toDto(department);
    }

    /// US-20 — alla patienter som just nu är inskrivna på en avdelning.
    @Transactional(readOnly = true)
    public List<DepartmentPatientDTO> getAdmittedPatients(Long departmentId) {

        if (!departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException(
                    "Avdelning med id: " + departmentId + " hittades inte");
        }

        List<CareContact> careContacts =
                careContactRepository.findByDepartmentIdAndStatus(departmentId, STATUS_ADMITTED);

        if (careContacts.isEmpty()) {
            return List.of();
        }

        List<Long> patientIds = careContacts.stream()
                .map(CareContact::getPatientId)
                .toList();

        Map<Long, Patient> patientsById = patientRepository.findAllById(patientIds).stream()
                .collect(Collectors.toMap(Patient::getId, Function.identity()));

        return careContacts.stream()
                .map(careContact -> {
                    Patient patient = patientsById.get(careContact.getPatientId());
                    if (patient == null || patient.getDeletedAt() != null) {
                        return null;
                    }
                    return new DepartmentPatientDTO(
                            patient.getId(),
                            patient.getPersonalNumber(),
                            patient.getFirstName(),
                            patient.getLastName(),
                            careContact.getId(),
                            careContact.getReason(),
                            careContact.getAdmitDate());
                })
                .filter(java.util.Objects::nonNull)
                .toList();
    }
}
