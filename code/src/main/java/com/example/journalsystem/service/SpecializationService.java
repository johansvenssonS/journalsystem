package com.example.journalsystem.service;

import com.example.journalsystem.dto.SpecializationDto;
import com.example.journalsystem.mapper.SpecializationMapper;
import com.example.journalsystem.repository.SpecializationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecializationService {

    private final SpecializationRepository specializationRepository;
    private final SpecializationMapper specializationMapper;

    public SpecializationService(SpecializationRepository specializationRepository, SpecializationMapper specializationMapper) {
        this.specializationRepository = specializationRepository;
        this.specializationMapper = specializationMapper;
    }

    public List<SpecializationDto> getAll() {
        return specializationRepository.findAll().stream()
                .map(specializationMapper::toDto)
                .toList();
    }
}
