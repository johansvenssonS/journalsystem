package com.example.journalsystem.service;

import com.example.journalsystem.dto.MedicalDashboardPatientDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientDashboardService {

    // audit_log.event is a fixed MySQL ENUM('read','created','updated','deleted') —
    // "opening a patient's journal" is a read of the patient record.
    private static final String AUDIT_ENTITY_TYPE = "patient";
    private static final String AUDIT_EVENT_OPENED = "read";

    private final PatientService patientService;
    private final CareContactService careContactService;
    private final JournalEntryService journalEntryService;
    private final DiagnosisService diagnosisService;
    private final AuditLogService auditLogService;
    private final HttpServletRequest request;

    public PatientDashboardService(PatientService patientService,
                                   CareContactService careContactService,
                                   JournalEntryService journalEntryService,
                                   DiagnosisService diagnosisService,
                                   AuditLogService auditLogService,
                                   HttpServletRequest request) {
        this.patientService = patientService;
        this.careContactService = careContactService;
        this.journalEntryService = journalEntryService;
        this.diagnosisService = diagnosisService;
        this.auditLogService = auditLogService;
        this.request = request;
    }

    /// US-53 — varje gång en patients journal öppnas loggas vem och när.
    @Transactional(readOnly = true)
    public MedicalDashboardPatientDTO getFullDashboardData(Long patientId) {
        MedicalDashboardPatientDTO dashboard = new MedicalDashboardPatientDTO();

        // 1. Patient basic details (US-33)
        dashboard.setPatient(patientService.getPatientById(patientId));

        // 2. Medical history lists (US-32)
        dashboard.setCareContacts(careContactService.getCareContactsByPatientId(patientId));
        dashboard.setJournalEntries(journalEntryService.getJournalEntriesByPatientId(patientId));
        dashboard.setDiagnoses(diagnosisService.getDiagnosisByPatientId(patientId));

        auditLogService.record(AUDIT_EVENT_OPENED, patientId, AUDIT_ENTITY_TYPE, request);

        return dashboard;
    }

















}