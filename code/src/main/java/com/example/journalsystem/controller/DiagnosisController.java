package com.example.journalsystem.controller;


import com.example.journalsystem.entities.DiagnosisDTO;
import com.example.journalsystem.service.DiagnosisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class DiagnosisController {
    private final DiagnosisService diagnosisService;

    public DiagnosisController(DiagnosisService diagnosisService) {
        this.diagnosisService = diagnosisService;
    }

    @GetMapping("diagnosis")
    public ResponseEntity<List<DiagnosisDTO>> getAllDiagnosis() {
        return ResponseEntity.ok(diagnosisService.getAllDiagnosis());
        
    }

    @GetMapping("diagnosis/{id}")
    public ResponseEntity<DiagnosisDTO> getDiagnosisById(@PathVariable Long id) {
        return ResponseEntity.ok(diagnosisService.getDiagnosisById(id));
    }

}
