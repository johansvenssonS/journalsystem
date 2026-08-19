package com.example.journalsystem.controller;

import com.example.journalsystem.dto.CreatePatientRequest;
import com.example.journalsystem.dto.PatientDetailResponse;
import com.example.journalsystem.dto.PatientResponse;
import com.example.journalsystem.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }
    @GetMapping
    public ResponseEntity<List<PatientResponse>> getAll(){
        return
                ResponseEntity.ok(patientService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @PreAuthorize("hasRole('RECEPTIONIST')")
    @PostMapping
    public ResponseEntity<PatientResponse> createPatient(@Valid @RequestBody CreatePatientRequest createPatientRequest) {
        return ResponseEntity.status(201).body(patientService.createPatient(createPatientRequest));
    }

    @GetMapping("/personal-number/{personalNumber}")
    public ResponseEntity<PatientResponse> getByPersonalNumber(@PathVariable String personalNumber) {
        return ResponseEntity.ok(patientService.getPatientByPersonalNumber(personalNumber));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<PatientDetailResponse> getPatientDetails(@PathVariable Long id){
        return ResponseEntity.ok(patientService.getPatientDetails(id));
    }
}













