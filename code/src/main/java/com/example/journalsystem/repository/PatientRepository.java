package com.example.journalsystem.repository;


import com.example.journalsystem.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {



}
