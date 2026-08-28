package com.example.journalsystem.service;

import com.example.journalsystem.dto.DepartmentPatientDTO;
import com.example.journalsystem.dto.StaffDTO;
import com.example.journalsystem.entities.CareContact;
import com.example.journalsystem.entities.Patient;
import com.example.journalsystem.entities.Staff;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.mapper.StaffMapper;
import com.example.journalsystem.repository.CareContactRepository;
import com.example.journalsystem.repository.PatientRepository;
import com.example.journalsystem.repository.StaffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StaffService {

    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;
    private final CareContactRepository careContactRepository;
    private final PatientRepository patientRepository;

    public StaffService(StaffRepository staffRepository, StaffMapper staffMapper,
                        CareContactRepository careContactRepository, PatientRepository patientRepository) {
        this.staffRepository = staffRepository;
        this.staffMapper = staffMapper;
        this.careContactRepository = careContactRepository;
        this.patientRepository = patientRepository;
    }

    public List<StaffDTO> getAll() {
        return staffRepository.findAll().stream()
                .map(staffMapper::toDto)
                .toList();
    }

    public StaffDTO getStaffById(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Personal med Id:" + id + " hittades inte"));
        return staffMapper.toDto(staff);
    }

    /// Patienter kopplade till en läkare/sjuksköterska via vårdkontakter där
    /// de står som ansvarig personal — underlag för en snabb "mina patienter"-vy
    /// i journalflödet. En rad per patient, byggd från deras senaste vårdkontakt.
    @Transactional(readOnly = true)
    public List<DepartmentPatientDTO> getAssignedPatients(Long staffId) {
        if (!staffRepository.existsById(staffId)) {
            throw new ResourceNotFoundException("Personal med id: " + staffId + " hittades inte");
        }

        List<CareContact> careContacts = careContactRepository.findByResponsibleStaffId(staffId);
        if (careContacts.isEmpty()) {
            return List.of();
        }

        Map<Long, CareContact> latestByPatient = new LinkedHashMap<>();
        for (CareContact careContact : careContacts) {
            latestByPatient.merge(careContact.getPatientId(), careContact,
                    (a, b) -> isAfter(a.getAdmitDate(), b.getAdmitDate()) ? a : b);
        }

        Map<Long, Patient> patientsById = patientRepository.findAllById(latestByPatient.keySet()).stream()
                .collect(Collectors.toMap(Patient::getId, Function.identity()));

        return latestByPatient.values().stream()
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
                .filter(Objects::nonNull)
                .toList();
    }

    private boolean isAfter(java.sql.Timestamp a, java.sql.Timestamp b) {
        if (a == null) return false;
        if (b == null) return true;
        return a.after(b);
    }
}
