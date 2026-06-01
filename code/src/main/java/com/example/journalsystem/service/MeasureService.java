package com.example.journalsystem.service;

import com.example.journalsystem.entities.CareContactDTO;
import com.example.journalsystem.entities.Measure;
import com.example.journalsystem.entities.MeasureDTO;
import com.example.journalsystem.entities.MeasureMapper;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.repository.MeasureRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeasureService {
    private final MeasureRepository measureRepository;
    private final MeasureMapper measureMapper;

    public MeasureService(MeasureRepository measureRepository, MeasureMapper measureMapper) {
        this.measureRepository = measureRepository;
        this.measureMapper = measureMapper;
    }

    public List<MeasureDTO> getAllMeasures() {
        return measureRepository.findAll().stream()
                .map(measure -> new MeasureDTO(
                        measure.getId(),
                        measure.getPerformedBy(),
                        measure.getDescription(),
                        measure.getPerformedAt()
                )).toList();
    }

    public  MeasureDTO getMeasureById(Long id){
        Measure measure = measureRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Vårdkontakt med id: "+ id + " hittades inte"));
        return measureMapper.toDto(measure);
    }
}
