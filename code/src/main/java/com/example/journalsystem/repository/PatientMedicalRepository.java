package com.example.journalsystem.repository;

import com.example.journalsystem.entities.PatientMedical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientMedicalRepository extends JpaRepository<PatientMedical, Long> {
}
