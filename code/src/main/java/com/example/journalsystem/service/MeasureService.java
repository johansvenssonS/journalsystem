package com.example.journalsystem.service;

import com.example.journalsystem.dto.CreateMeasureRequest;
import com.example.journalsystem.dto.MeasureResponse;
import com.example.journalsystem.entities.Measure;
import com.example.journalsystem.entities.MeasureDTO;
import com.example.journalsystem.entities.MeasureMapper;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.repository.JournalRepository;
import com.example.journalsystem.repository.MeasureRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class MeasureService {
    private final MeasureRepository measureRepository;
    private final MeasureMapper measureMapper;
    private final JournalRepository journalRepository;

    public MeasureService(MeasureRepository measureRepository, MeasureMapper measureMapper,
                           JournalRepository journalRepository) {
        this.measureRepository = measureRepository;
        this.measureMapper = measureMapper;
        this.journalRepository = journalRepository;
    }

    public List<MeasureDTO> getAllMeasures() {
        return measureRepository.findAll().stream()
                .map(measureMapper::toDto)
                .toList();
    }

    public MeasureDTO getMeasureById(Long id){
        Measure measure = measureRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Åtgärd med id: "+ id + " hittades inte"));
        return measureMapper.toDto(measure);
    }

    public MeasureResponse createMeasure(CreateMeasureRequest createMeasureRequest){
        Long journalEntryId = createMeasureRequest.getJournalEntryId();

        if (!journalRepository.existsById(journalEntryId)) {
            throw new ResourceNotFoundException("Journalpost med id: " + journalEntryId + " hittades inte");
        }

        Measure measure = new Measure();
        measure.setJournalEntryId(journalEntryId);
        measure.setPerformedBy(createMeasureRequest.getPerformedBy());
        measure.setDescription(createMeasureRequest.getDescription());
        measure.setPerformedDate(createMeasureRequest.getPerformedDate() != null
                ? createMeasureRequest.getPerformedDate()
                : new Date(System.currentTimeMillis()));

        Measure saved = measureRepository.save(measure);

        return new MeasureResponse(
                saved.getId(),
                saved.getJournalEntryId(),
                saved.getPerformedBy(),
                saved.getDescription(),
                saved.getPerformedDate()
        );
    }
}
