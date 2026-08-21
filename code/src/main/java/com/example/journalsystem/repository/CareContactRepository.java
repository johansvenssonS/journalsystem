package com.example.journalsystem.repository;

import com.example.journalsystem.entities.CareContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareContactRepository extends JpaRepository<CareContact, Long> {
    List<CareContact> findByPatientId(Long patientId);
}
