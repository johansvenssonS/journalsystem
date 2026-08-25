package com.example.journalsystem.repository;

import com.example.journalsystem.entities.Entity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EntityRepository extends JpaRepository<Entity, Long> {
    Optional<Entity> findByEntity(String entity);
}