package com.example.journalsystem.controller;


import com.example.journalsystem.entities.Sjukhus;
import com.example.journalsystem.service.HospitalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class HospitalController {
    private final HospitalService hospitalService;


    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }


    @GetMapping("/sjukhus")
    public ResponseEntity <List<Sjukhus>> getAllHospitals(){
        return ResponseEntity.ok(hospitalService.getAllHospitals());
    }
}
