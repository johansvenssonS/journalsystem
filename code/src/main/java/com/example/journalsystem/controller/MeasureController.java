package com.example.journalsystem.controller;


import com.example.journalsystem.dto.CreateMeasureRequest;
import com.example.journalsystem.dto.MeasureResponse;
import com.example.journalsystem.entities.MeasureDTO;
import com.example.journalsystem.service.MeasureService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/measure")
public class MeasureController {
    private final MeasureService measureService;


    public MeasureController(MeasureService measureService) {
        this.measureService = measureService;
    }

    @GetMapping()
    public ResponseEntity<List<MeasureDTO>> getAllMeasures() {
        return ResponseEntity.ok(measureService.getAllMeasures());
        
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeasureDTO> getMeasureById(@PathVariable Long id) {
        return ResponseEntity.ok(measureService.getMeasureById(id));

    }

    @PreAuthorize("hasRole('doctor') OR hasRole('nurse')")
    @PostMapping()
    public ResponseEntity<MeasureResponse> createMeasure(@Valid @RequestBody CreateMeasureRequest createMeasureRequest){
        return ResponseEntity.status(201).body(measureService.createMeasure(createMeasureRequest));
    }
}
