package com.example.journalsystem.controller;

import com.example.journalsystem.dto.PrescriptionDTO;
import com.example.journalsystem.service.PrescriptionService;
import org.springframework.http.ResponseEntity;
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
}