package com.example.journalsystem.repository;

import com.example.journalsystem.entities.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JournalRepository extends JpaRepository<JournalEntry, Long> {
    List<JournalEntry> findByCareContact_PatientId(Long patientId);
}
