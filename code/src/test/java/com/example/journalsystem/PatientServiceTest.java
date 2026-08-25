package com.example.journalsystem;

import com.example.journalsystem.dto.CreatePatientRequest;
import com.example.journalsystem.dto.PatientResponse;
import com.example.journalsystem.entities.Patient;
import com.example.journalsystem.entities.PatientContact;
import com.example.journalsystem.exceptions.DuplicateResourceException;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.mapper.PatientMapper;
import com.example.journalsystem.repository.PatientContactRepository;
import com.example.journalsystem.repository.PatientRepository;
import com.example.journalsystem.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientMapper patientMapper;

    @Mock
    private PatientContactRepository patientContactRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;
    private PatientResponse patientResponse;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setPersonalNumber("199001011234");
        patient.setFirstName("Anna");
        patient.setLastName("Andersson");

        patientResponse = new PatientResponse(1L, "199001011234", "Anna", "Andersson", null);
    }

    // ==========================================
    // Tests for getAll()
    // ==========================================

    @Test
    @DisplayName("getAll - ska returnera en lista med alla patienter som DTOs")
    void getAll_ShouldReturnListOfPatientResponses() {
        // Arrange
        when(patientRepository.findAll()).thenReturn(List.of(patient));
        when(patientMapper.toDto(patient)).thenReturn(patientResponse);

        // Act
        List<PatientResponse> result = patientService.getAll();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPersonalNumber()).isEqualTo("199001011234");
        verify(patientRepository, times(1)).findAll();
        verify(patientMapper, times(1)).toDto(patient);
    }

    // ==========================================
    // Tests for getPatientById()
    // ==========================================

    @Test
    @DisplayName("getPatientById - ska returnera patient när ID finns")
    void getPatientById_WhenPatientExists_ShouldReturnPatientResponse() {
        // Arrange
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientMapper.toDto(patient)).thenReturn(patientResponse);

        // Act
        PatientResponse result = patientService.getPatientById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("Anna");
        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getPatientById - ska kasta ResourceNotFoundException när patient inte finns")
    void getPatientById_WhenPatientDoesNotExist_ShouldThrowException() {
        // Arrange
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> patientService.getPatientById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Patient med Id: 99 hittades inte");

        verify(patientRepository, times(1)).findById(99L);
        verifyNoInteractions(patientMapper);
    }

    // ==========================================
    // Tests for createPatient()
    // ==========================================

    @Test
    @DisplayName("createPatient - ska skapa och returnera ny patient när personnummer är unikt")
    void createPatient_WhenPersonalNumberIsUnique_ShouldSaveAndReturnPatient() {
        // Arrange
        CreatePatientRequest request = new CreatePatientRequest("199001011234", "Anna", "Andersson");

        when(patientRepository.existsByPersonalNumber(request.getPersonalNumber())).thenReturn(false);
        when(patientMapper.toEntity(request)).thenReturn(patient);
        when(patientRepository.save(patient)).thenReturn(patient);
        when(patientMapper.toDto(patient)).thenReturn(patientResponse);
        when(patientContactRepository.save(any(PatientContact.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        PatientResponse result = patientService.createPatient(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPersonalNumber()).isEqualTo(request.getPersonalNumber());
        verify(patientRepository, times(1)).existsByPersonalNumber(request.getPersonalNumber());
        verify(patientRepository, times(1)).save(patient);
        verify(patientContactRepository, times(1)).save(any(PatientContact.class));
    }

    @Test
    @DisplayName("createPatient - ska kasta DuplicateResourceException om personnumret redan finns")
    void createPatient_WhenPersonalNumberAlreadyExists_ShouldThrowException() {
        // Arrange
        CreatePatientRequest request = new CreatePatientRequest("199001011234", "Anna", "Andersson");

        when(patientRepository.existsByPersonalNumber(request.getPersonalNumber())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> patientService.createPatient(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("En patient med personnumret 199001011234 finns redan");

        verify(patientRepository, times(1)).existsByPersonalNumber(request.getPersonalNumber());
        verify(patientRepository, never()).save(any());
        verifyNoInteractions(patientMapper);
    }
}