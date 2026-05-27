package com.example.journalsystem.mapper;

import com.example.journalsystem.dto.SpecializationDto;
import com.example.journalsystem.entities.Specialization;
import org.springframework.stereotype.Component;

@Component
public class SpecializationMapper {

    public SpecializationDto toDto(Specialization specialization) {

        return new SpecializationDto(
                specialization.getId(),
                specialization.getName()
        );
    }
}
