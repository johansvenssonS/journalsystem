package com.example.journalsystem.controller;


import com.example.journalsystem.dto.PatientContactDto;
import com.example.journalsystem.service.PatientContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
