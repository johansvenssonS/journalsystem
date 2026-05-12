package com.example.journalsystem.repository;

import com.example.journalsystem.entities.Sjukhus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SjukhusRepository extends JpaRepository<Sjukhus, Long> {
}
