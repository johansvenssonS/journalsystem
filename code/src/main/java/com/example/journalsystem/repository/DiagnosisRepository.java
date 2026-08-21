package com.example.journalsystem.repository;

import com.example.journalsystem.entities.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagnosisRepository extends JpaRepository<Diagnosis,Long> {
    List<Diagnosis> findByJournalEntry_CareContact_PatientId(Long patientId);
}
