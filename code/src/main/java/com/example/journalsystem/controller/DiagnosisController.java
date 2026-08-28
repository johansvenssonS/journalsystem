package com.example.journalsystem.controller;


import com.example.journalsystem.dto.CreateDiagnosisRequest;
import com.example.journalsystem.dto.DiagnosisResponse;
import com.example.journalsystem.entities.DiagnosisDTO;
import com.example.journalsystem.service.DiagnosisService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/diagnosis")
public class DiagnosisController {
    private final DiagnosisService diagnosisService;

    public DiagnosisController(DiagnosisService diagnosisService) {
        this.diagnosisService = diagnosisService;
    }

    @PreAuthorize("hasAnyRole('doctor','nurse')")
    @GetMapping()
    public ResponseEntity<List<DiagnosisDTO>> getAllDiagnosis() {
        return ResponseEntity.ok(diagnosisService.getAllDiagnosis());
        
    }

    @PreAuthorize("hasAnyRole('doctor','nurse')")
    @GetMapping("/{id}")
    public ResponseEntity<DiagnosisDTO> getDiagnosisById(@PathVariable Long id) {
        return ResponseEntity.ok(diagnosisService.getDiagnosisById(id));
    }

    //Metod för att skapa diagnos med journal_entry_id
    @PreAuthorize("hasRole('doctor')")
    @PostMapping()
    public ResponseEntity<DiagnosisResponse> createDiagnosis(@Valid @RequestBody CreateDiagnosisRequest createDiagnosisRequest){
        return ResponseEntity.status(201).body(diagnosisService.createDiagnosis(createDiagnosisRequest));
    }

}
