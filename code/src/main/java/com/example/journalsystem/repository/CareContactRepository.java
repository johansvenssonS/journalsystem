package com.example.journalsystem.repository;

import com.example.journalsystem.entities.CareContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareContactRepository extends JpaRepository<CareContact, Long> {
    List<CareContact> findByPatientId(Long patientId);

    /// US-20 — vårdkontakter på en avdelning med en viss status,
    /// t.ex. alla som just nu är inskrivna.
    List<CareContact> findByDepartmentIdAndStatus(Long departmentId, String status);
}
