package com.example.journalsystem.repository;

import com.example.journalsystem.entities.StaffEmployment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StaffEmploymentRepository extends JpaRepository<StaffEmployment, Long> {

}