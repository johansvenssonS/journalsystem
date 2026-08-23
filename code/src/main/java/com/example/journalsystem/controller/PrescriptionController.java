package com.example.journalsystem.controller;

import com.example.journalsystem.dto.CreatePrescriptionRequest;
import com.example.journalsystem.dto.PrescriptionDTO;
import com.example.journalsystem.dto.PrescriptionResponse;
import com.example.journalsystem.service.PrescriptionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @GetMapping
    public ResponseEntity<List<PrescriptionDTO>> getAll() {
        return ResponseEntity.ok(prescriptionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionById(id));
    }

    @PreAuthorize("hasRole('doctor')")
    @PostMapping
    public ResponseEntity<PrescriptionResponse> createPrescription(@Valid @RequestBody CreatePrescriptionRequest createPrescriptionRequest){
        return ResponseEntity.status(201).body(prescriptionService.createPrescription(createPrescriptionRequest));
    }
}