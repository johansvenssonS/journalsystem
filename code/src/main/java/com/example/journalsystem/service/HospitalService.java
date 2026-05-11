package com.example.journalsystem.service;


import com.example.journalsystem.entities.Sjukhus;
import com.example.journalsystem.repository.HospitalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HospitalService {
    public final HospitalRepository hospitalRepository;


    public HospitalService(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }



    public List<Sjukhus> getAllHospitals(){
        return hospitalRepository.findAll();
    }
}
