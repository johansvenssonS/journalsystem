package com.example.journalsystem.service;


import com.example.journalsystem.entities.Avdelning;
import com.example.journalsystem.entities.Sjukhus;
import com.example.journalsystem.entities.SjukhusDTO;
import com.example.journalsystem.repository.SjukhusRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SjukhusService {
    public final SjukhusRepository sjukhusRepository;


    public SjukhusService(SjukhusRepository sjukhusRepository) {
        this.sjukhusRepository = sjukhusRepository;
    }



    public List<SjukhusDTO> getAllHospitals(){
        return sjukhusRepository.findAll().stream()
                .map(sjukhus -> new SjukhusDTO(
                        sjukhus.getId(),
                        sjukhus.getNamn(),
                        sjukhus.getAdress(),
                        sjukhus.getTelefon(),
                        sjukhus.getRegion().getNamn(),
                        sjukhus.getAvdelningar().stream().map(Avdelning::getNamn).toList()
                ))
                .toList();
    }
}
