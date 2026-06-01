package com.example.journalsystem.repository;

import com.example.journalsystem.entities.CareContact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareContactRepository extends JpaRepository<CareContact, Long> {
}
