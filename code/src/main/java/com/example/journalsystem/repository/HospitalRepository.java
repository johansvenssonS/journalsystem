package com.example.journalsystem.repository;

import com.example.journalsystem.entities.Sjukhus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalRepository extends JpaRepository<Sjukhus, Long> {
}
