package com.example.journalsystem.controller;


import com.example.journalsystem.dto.PatientMedicalDto;
import com.example.journalsystem.service.PatientMedicalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
