package com.example.journalsystem.repository;

import com.example.journalsystem.entities.Referral;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferralRepository extends JpaRepository<Referral, Long> {
}
