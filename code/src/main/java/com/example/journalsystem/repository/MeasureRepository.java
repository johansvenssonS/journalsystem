package com.example.journalsystem.repository;

import com.example.journalsystem.entities.Measure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeasureRepository extends JpaRepository<Measure, Long> {
}
