// ========== REPOSITORY ==========
package com.example.journalsystem.repository;

import com.example.journalsystem.entities.StaffContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StaffContactRepository extends JpaRepository<StaffContact, Long> {

}