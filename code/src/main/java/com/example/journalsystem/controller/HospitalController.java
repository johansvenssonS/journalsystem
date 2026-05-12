package com.example.journalsystem.controller;


import com.example.journalsystem.entities.Sjukhus;
import com.example.journalsystem.entities.SjukhusDTO;
import com.example.journalsystem.service.SjukhusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping
public class HospitalController {
    private final SjukhusService sjukhusService;


    public HospitalController(SjukhusService sjukhusService) {
        this.sjukhusService = sjukhusService;
    }


    @GetMapping("/sjukhus")
    public ResponseEntity <List<SjukhusDTO>> getAllHospitals(){
        return ResponseEntity.ok(sjukhusService.getAllHospitals());
    }
}
