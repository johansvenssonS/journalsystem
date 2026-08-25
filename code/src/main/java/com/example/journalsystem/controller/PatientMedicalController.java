package com.example.journalsystem.controller;


import com.example.journalsystem.dto.PatientMedicalDto;
import com.example.journalsystem.service.PatientMedicalService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patient-medicals")
public class PatientMedicalController {

    private final PatientMedicalService patientMedicalService;
    public PatientMedicalController(PatientMedicalService patientMedicalService) {
        this.patientMedicalService = patientMedicalService;
    }

    @GetMapping
    public ResponseEntity<List<PatientMedicalDto>> getAll()  {
        return ResponseEntity.ok(patientMedicalService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientMedicalDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(patientMedicalService.getPatientMedicalById(id));
    }

    @PreAuthorize("hasRole('doctor') OR hasRole('nurse')")
    @PutMapping("/{id}")
    public ResponseEntity<PatientMedicalDto> update(
            @PathVariable Long id,
            @RequestBody PatientMedicalDto dto) {
        return ResponseEntity.ok(patientMedicalService.update(id, dto));
    }

}
