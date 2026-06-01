package com.example.journalsystem.repository;

import com.example.journalsystem.entities.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalRepository extends JpaRepository<JournalEntry, Long> {
}
