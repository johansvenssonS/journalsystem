package com.example.journalsystem.repository;

import com.example.journalsystem.entities.Diagnosis;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosisRepository extends JpaRepository<Diagnosis,Long > {
}
