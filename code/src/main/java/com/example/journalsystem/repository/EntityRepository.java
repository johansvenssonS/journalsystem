package com.example.journalsystem.repository;

import com.example.journalsystem.entities.Entity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntityRepository extends JpaRepository<Entity, Long> {
}