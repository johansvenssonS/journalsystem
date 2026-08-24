package com.example.journalsystem;

import com.example.journalsystem.dto.DepartmentPatientDTO;
import com.example.journalsystem.entities.CareContact;
import com.example.journalsystem.entities.Patient;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.mapper.DepartmentMapper;
import com.example.journalsystem.repository.CareContactRepository;
import com.example.journalsystem.repository.DepartmentRepository;
import com.example.journalsystem.repository.PatientRepository;
import com.example.journalsystem.repository.SpecializationRepository;
import com.example.journalsystem.service.DepartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/// US-20 — Som sjuksköterska vill jag kunna se alla patienter som är
/// inskrivna på min avdelning så att jag har överblick över mitt ansvarsområde.
@ExtendWith(MockitoExtension.class)
class DepartmentPatientsTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private SpecializationRepository specializationRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @Mock
    private CareContactRepository careContactRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private DepartmentService departmentService;

    private CareContact careContact;
    private Patient patient;

    @BeforeEach
    void setUp() {
        careContact = new CareContact();
        careContact.setId(10L);
        careContact.setPatientId(1L);
        careContact.setDepartmentId(2L);
        careContact.setReason("Bröstsmärta");
        careContact.setAdmitDate(Timestamp.valueOf("2026-08-20 08:00:00"));
        careContact.setStatus(DepartmentService.STATUS_ADMITTED);

        patient = new Patient();
        patient.setId(1L);
        patient.setPersonalNumber("194506127890");
        patient.setFirstName("Sven");
        patient.setLastName("Gustafsson");
    }

    // ==========================================
    // Tests for getAdmittedPatients()
    // ==========================================

    @Test
    @DisplayName("getAdmittedPatients - ska returnera inskrivna patienter med uppgifter från vårdkontakten")
    void getAdmittedPatients_WhenPatientsAreAdmitted_ShouldReturnThem() {
        // Arrange
        when(departmentRepository.existsById(2L)).thenReturn(true);
        when(careContactRepository.findByDepartmentIdAndStatus(2L, DepartmentService.STATUS_ADMITTED))
                .thenReturn(List.of(careContact));
        when(patientRepository.findAllById(List.of(1L))).thenReturn(List.of(patient));

        // Act
        List<DepartmentPatientDTO> result = departmentService.getAdmittedPatients(2L);

        // Assert
        assertThat(result).hasSize(1);
        DepartmentPatientDTO dto = result.get(0);
        assertThat(dto.getPatientId()).isEqualTo(1L);
        assertThat(dto.getFirstName()).isEqualTo("Sven");
        assertThat(dto.getLastName()).isEqualTo("Gustafsson");
        assertThat(dto.getCareContactId()).isEqualTo(10L);
        assertThat(dto.getReason()).isEqualTo("Bröstsmärta");
        assertThat(dto.getAdmitDate()).isEqualTo(Timestamp.valueOf("2026-08-20 08:00:00"));
    }

    @Test
    @DisplayName("getAdmittedPatients - ska returnera tom lista när ingen är inskriven på avdelningen")
    void getAdmittedPatients_WhenNoneAdmitted_ShouldReturnEmptyList() {
        // Arrange
        when(departmentRepository.existsById(2L)).thenReturn(true);
        when(careContactRepository.findByDepartmentIdAndStatus(2L, DepartmentService.STATUS_ADMITTED))
                .thenReturn(List.of());

        // Act
        List<DepartmentPatientDTO> result = departmentService.getAdmittedPatients(2L);

        // Assert
        assertThat(result).isEmpty();
        verify(patientRepository, never()).findAllById(any());
    }

    @Test
    @DisplayName("getAdmittedPatients - ska kasta ResourceNotFoundException när avdelningen inte finns")
    void getAdmittedPatients_WhenDepartmentDoesNotExist_ShouldThrowException() {
        // Arrange
        when(departmentRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> departmentService.getAdmittedPatients(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Avdelning med id: 99 hittades inte");

        verifyNoInteractions(careContactRepository);
    }

    @Test
    @DisplayName("getAdmittedPatients - ska utelämna patienter som är mjukraderade")
    void getAdmittedPatients_WhenPatientIsSoftDeleted_ShouldSkipThem() {
        // Arrange
        patient.setDeletedAt(Instant.parse("2026-08-01T00:00:00Z"));

        when(departmentRepository.existsById(2L)).thenReturn(true);
        when(careContactRepository.findByDepartmentIdAndStatus(2L, DepartmentService.STATUS_ADMITTED))
                .thenReturn(List.of(careContact));
        when(patientRepository.findAllById(List.of(1L))).thenReturn(List.of(patient));

        // Act
        List<DepartmentPatientDTO> result = departmentService.getAdmittedPatients(2L);

        // Assert
        assertThat(result).isEmpty();
    }
}
