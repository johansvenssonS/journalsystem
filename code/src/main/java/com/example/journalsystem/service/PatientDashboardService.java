package com.example.journalsystem.service;

import com.example.journalsystem.dto.MedicalDashboardPatientDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientDashboardService {

    private final PatientService patientService;
    private final CareContactService careContactService;
    private final JournalEntryService journalEntryService;
    private final DiagnosisService diagnosisService;

    public PatientDashboardService(PatientService patientService,
                                   CareContactService careContactService,
                                   JournalEntryService journalEntryService,
                                   DiagnosisService diagnosisService) {
        this.patientService = patientService;
        this.careContactService = careContactService;
        this.journalEntryService = journalEntryService;
        this.diagnosisService = diagnosisService;
    }

    @Transactional(readOnly = true)
    public MedicalDashboardPatientDTO getFullDashboardData(Long patientId) {
        MedicalDashboardPatientDTO dashboard = new MedicalDashboardPatientDTO();

        // 1. Patient basic details (US-33)
        dashboard.setPatient(patientService.getPatientById(patientId));

        // 2. Medical history lists (US-32)
        dashboard.setCareContacts(careContactService.getCareContactsByPatientId(patientId));
        dashboard.setJournalEntries(journalEntryService.getJournalEntriesByPatientId(patientId));
        dashboard.setDiagnoses(diagnosisService.getDiagnosisByPatientId(patientId));

        return dashboard;
    }

















}