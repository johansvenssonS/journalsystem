package com.example.journalsystem.repository;

import com.example.journalsystem.entities.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByPatientIdOrderByOccurredAtDesc(Long patientId);
}