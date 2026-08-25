package com.example.journalsystem.service;


import com.example.journalsystem.dto.CreateCareContactRequest;
import com.example.journalsystem.dto.DischargeCareContactRequest;
import com.example.journalsystem.entities.*;
import com.example.journalsystem.exceptions.DuplicateResourceException;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.repository.CareContactRepository;
import com.example.journalsystem.repository.DepartmentRepository;
import com.example.journalsystem.repository.PatientRepository;
import com.example.journalsystem.repository.StaffRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class CareContactService {

    public static final String STATUS_PLANNED = "planned";
    public static final String STATUS_ADMITTED = "admitted";
    public static final String STATUS_DISCHARGED = "discharged";

    public final CareContactRepository careContactRepository;
    public final CareContactMapper careContactMapper;
    private final PatientRepository patientRepository;
    private final DepartmentRepository departmentRepository;
    private final StaffRepository staffRepository;

    public CareContactService(CareContactMapper careContactMapper,
                              CareContactRepository careContactRepository,
                              PatientRepository patientRepository,
                              DepartmentRepository departmentRepository,
                              StaffRepository staffRepository) {
        this.careContactRepository = careContactRepository;
        this.careContactMapper = careContactMapper;
        this.patientRepository = patientRepository;
        this.departmentRepository = departmentRepository;
        this.staffRepository = staffRepository;
    }

    public List<CareContactDTO> getAllCareContacts(){
        return careContactRepository.findAll().stream()
                .map(careContactMapper::toDto)
                .toList();
    }

    public CareContactDTO getCareContactById(Long id){
        CareContact careContact = careContactRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Vårdkontakt med id: "+ id + " hittades inte"));
        return careContactMapper.toDto(careContact);
    }

    public List<CareContactDTO> getCareContactsByPatientId(Long patientId) {
        return careContactRepository.findByPatientId(patientId)
                .stream()
                .map(careContactMapper::toDto)
                .toList();
    }

    /// US-12 — skapar en ny vårdkontakt för en patient på en avdelning.
    @Transactional
    public CareContactDTO createCareContact(CreateCareContactRequest request) {

        if (!patientRepository.existsById(request.getPatientId())) {
            throw new ResourceNotFoundException(
                    "Patient med id: " + request.getPatientId() + " hittades inte");
        }
        if (!departmentRepository.existsById(request.getDepartmentId())) {
            throw new ResourceNotFoundException(
                    "Avdelning med id: " + request.getDepartmentId() + " hittades inte");
        }
        if (!staffRepository.existsById(request.getResponsibleStaffId())) {
            throw new ResourceNotFoundException(
                    "Personal med id: " + request.getResponsibleStaffId() + " hittades inte");
        }

        CareContact careContact = new CareContact();
        careContact.setPatientId(request.getPatientId());
        careContact.setDepartmentId(request.getDepartmentId());
        careContact.setResponsibleStaffId(request.getResponsibleStaffId());
        careContact.setReason(request.getReason());
        careContact.setAdmitDate(Timestamp.from(Instant.now()));
        careContact.setStatus(STATUS_PLANNED);

        return careContactMapper.toDto(careContactRepository.save(careContact));
    }

    /// Läkaren lägger in patienten efter ett planerat besök — sätter status till
    /// admitted och uppdaterar admit_date till den faktiska inskrivningstidpunkten
    /// (inte tidpunkten då vårdkontakten bokades).
    @Transactional
    public CareContactDTO admitCareContact(Long id) {

        CareContact careContact = careContactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vårdkontakt med id: " + id + " hittades inte"));

        if (!STATUS_PLANNED.equals(careContact.getStatus())) {
            throw new DuplicateResourceException(
                    "Vårdkontakt med id: " + id + " kan inte läggas in — status är redan " + careContact.getStatus());
        }

        careContact.setStatus(STATUS_ADMITTED);
        careContact.setAdmitDate(Timestamp.from(Instant.now()));

        return careContactMapper.toDto(careContactRepository.save(careContact));
    }

    /// US-17 — skriver ut en patient genom att sätta vårdkontaktens status
    /// till discharged och fylla i utskrivningsdatum. Kan ske direkt från planned
    /// (ett besök som aldrig krävde inläggning) eller från admitted.
    @Transactional
    public CareContactDTO dischargeCareContact(Long id, DischargeCareContactRequest request) {

        CareContact careContact = careContactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vårdkontakt med id: " + id + " hittades inte"));

        if (STATUS_DISCHARGED.equals(careContact.getStatus())) {
            throw new DuplicateResourceException(
                    "Vårdkontakt med id: " + id + " är redan utskriven");
        }

        Timestamp dischargeDate = (request != null && request.getDischargeDate() != null)
                ? Timestamp.valueOf(request.getDischargeDate())
                : Timestamp.from(Instant.now());

        careContact.setStatus(STATUS_DISCHARGED);
        careContact.setDischargeDate(dischargeDate);

        return careContactMapper.toDto(careContactRepository.save(careContact));
    }
}
