package com.example.journalsystem.repository;

import com.example.journalsystem.entities.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByPatient_PersonalNumber(String personalNumber);
}