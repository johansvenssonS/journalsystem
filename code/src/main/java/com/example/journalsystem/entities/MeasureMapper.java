package com.example.journalsystem.entities;


import org.springframework.stereotype.Component;

@Component
public class MeasureMapper {
    public MeasureDTO toDto(Measure measure){
        return new MeasureDTO(
                measure.getId(),
                measure.getPerformedBy(),
                measure.getDescription(),
                measure.getPerformedAt()
        );
    }
}
