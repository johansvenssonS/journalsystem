package com.example.journalsystem.controller;

import com.example.journalsystem.dto.AuditLogDTO;
import com.example.journalsystem.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/// US-53 — auditloggen rör alla patienter/personal i systemet, så den är
/// begränsad till receptionisten (systemets admin-motsvarande roll, se access.js).
@PreAuthorize("hasRole('RECEPTIONIST')")
@RestController
@RequestMapping("/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<List<AuditLogDTO>> getAll() {
        return ResponseEntity.ok(auditLogService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLogDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(auditLogService.getAuditLogById(id));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AuditLogDTO>> getByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(auditLogService.getByPatientId(patientId));
    }
}