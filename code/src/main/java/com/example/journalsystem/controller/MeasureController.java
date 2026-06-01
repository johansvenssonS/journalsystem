package com.example.journalsystem.controller;


import com.example.journalsystem.entities.MeasureDTO;
import com.example.journalsystem.service.MeasureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class MeasureController {
    private final MeasureService measureService;


    public MeasureController(MeasureService measureService) {
        this.measureService = measureService;
    }

    @GetMapping("measure")
    public ResponseEntity<List<MeasureDTO>> getAllMeasures() {
        return ResponseEntity.ok(measureService.getAllMeasures());
        
    }

    @GetMapping("measure/{id}")
    public ResponseEntity<MeasureDTO> getMeasureById(@PathVariable Long id) {
        return ResponseEntity.ok(measureService.getMeasureById(id));


    }
}
