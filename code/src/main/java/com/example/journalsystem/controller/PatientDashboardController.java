package com.example.journalsystem.controller;

import com.example.journalsystem.dto.MedicalDashboardPatientDTO;
import com.example.journalsystem.service.PatientDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/patients")
public class PatientDashboardController {

    private final PatientDashboardService patientDashboardService;

    public PatientDashboardController(PatientDashboardService patientDashboardService) {
        this.patientDashboardService = patientDashboardService;
    }

    @GetMapping("/{patientId}/dashboard")
    public ResponseEntity<MedicalDashboardPatientDTO> getPatientDashboard(@PathVariable Long patientId) {
        MedicalDashboardPatientDTO dashboardData = patientDashboardService.getFullDashboardData(patientId);
        return ResponseEntity.ok(dashboardData);
    }
}