package com.example.journalsystem.controller;


import com.example.journalsystem.dto.PatientContactDto;
import com.example.journalsystem.service.PatientContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patient-contacts")
public class PatientContactController {

    private final PatientContactService patientContactService;

    public PatientContactController(PatientContactService patientContactService) {
        this.patientContactService = patientContactService;
    }

    @GetMapping
    public ResponseEntity<List<PatientContactDto>> getAll() {
        return ResponseEntity.ok(patientContactService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientContactDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(patientContactService.getPatientContactById(id));
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    @PutMapping("/{id}")
    public ResponseEntity<PatientContactDto> update(
            @PathVariable Long id,
            @RequestBody PatientContactDto dto) {
        return ResponseEntity.ok(patientContactService.update(id, dto));
    }

}
