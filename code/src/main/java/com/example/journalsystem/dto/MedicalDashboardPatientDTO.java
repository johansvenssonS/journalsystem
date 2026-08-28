package com.example.journalsystem.dto;

import com.example.journalsystem.entities.CareContactDTO;
import com.example.journalsystem.entities.DiagnosisDTO;
import com.example.journalsystem.entities.JournalEntryDTO;

import java.util.List;

public class MedicalDashboardPatientDTO {
    private PatientResponse patient;
    private List<CareContactDTO> careContacts;
    private List<JournalEntryDTO> journalEntries;
    private List<DiagnosisDTO> diagnoses;

    public MedicalDashboardPatientDTO() {
    }

    public MedicalDashboardPatientDTO(PatientResponse patient, List<CareContactDTO> careContacts, List<JournalEntryDTO> journalEntries, List<DiagnosisDTO> diagnoses) {
        this.patient = patient;
        this.careContacts = careContacts;
        this.journalEntries = journalEntries;
        this.diagnoses = diagnoses;
    }

    public PatientResponse getPatient() {
        return patient;
    }

    public void setPatient(PatientResponse patient) {
        this.patient = patient;
    }

    public List<CareContactDTO> getCareContacts() {
        return careContacts;
    }

    public void setCareContacts(List<CareContactDTO> careContacts) {
        this.careContacts = careContacts;
    }

    public List<JournalEntryDTO> getJournalEntries() {
        return journalEntries;
    }

    public void setJournalEntries(List<JournalEntryDTO> journalEntries) {
        this.journalEntries = journalEntries;
    }

    public List<DiagnosisDTO> getDiagnoses() {
        return diagnoses;
    }

    public void setDiagnoses(List<DiagnosisDTO> diagnoses) {
        this.diagnoses = diagnoses;
    }
}

