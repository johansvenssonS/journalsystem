package com.example.journalsystem.repository;

import com.example.journalsystem.entities.PatientContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientContactRepository extends JpaRepository<PatientContact, Long> {
}
