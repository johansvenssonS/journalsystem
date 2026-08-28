package com.example.journalsystem;

import com.example.journalsystem.dto.CreateDiagnosisRequest;
import com.example.journalsystem.dto.DiagnosisResponse;
import com.example.journalsystem.entities.Diagnosis;
import com.example.journalsystem.entities.DiagnosisDTO;
import com.example.journalsystem.entities.DiagnosisMapper;
import com.example.journalsystem.entities.JournalEntry;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.repository.DiagnosisRepository;
import com.example.journalsystem.repository.JournalRepository;
import com.example.journalsystem.service.DiagnosisService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiagnosisServiceTest {

    @Mock
    private DiagnosisRepository diagnosisRepository;

    @Mock
    private DiagnosisMapper diagnosisMapper;

    @Mock
    private JournalRepository journalRepository;

    @InjectMocks
    private DiagnosisService diagnosisService;

    private JournalEntry journalEntry;

    @BeforeEach
    void setUp() {
        journalEntry = new JournalEntry();
        journalEntry.setId(10L);
    }

    // ==========================================
    // Tests for createDiagnosis() - the "original" flow
    // ==========================================

    @Test
    @DisplayName("createDiagnosis - ska koppla diagnosen till JournalEntry-relationen när journalEntryId finns")
    void createDiagnosis_WhenJournalEntryExists_ShouldSaveWithJournalEntryRelation() {
        // Arrange
        CreateDiagnosisRequest request = new CreateDiagnosisRequest();
        request.setJournalEntryId(10L);
        request.setSetBy(5L);
        request.setIcd10Code("J45.9");
        request.setName("Astma");
        request.setDescription("Lindrig astma");
        request.setDiagnosedDate(Date.valueOf("2026-08-20"));

        when(journalRepository.findById(10L)).thenReturn(Optional.of(journalEntry));
        when(diagnosisRepository.save(any(Diagnosis.class))).thenAnswer(invocation -> {
            Diagnosis d = invocation.getArgument(0);
            d.setId(1L);
            return d;
        });

        // Act
        DiagnosisResponse result = diagnosisService.createDiagnosis(request);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getJournalEntryId()).isEqualTo(10L);
        assertThat(result.getIcd10Code()).isEqualTo("J45.9");

        verify(journalRepository, times(1)).findById(10L);

        var captor = org.mockito.ArgumentCaptor.forClass(Diagnosis.class);
        verify(diagnosisRepository).save(captor.capture());
        assertThat(captor.getValue().getJournalEntry()).isSameAs(journalEntry);
    }

    @Test
    @DisplayName("createDiagnosis - ska kasta ResourceNotFoundException när journalEntryId inte finns")
    void createDiagnosis_WhenJournalEntryMissing_ShouldThrowException() {
        // Arrange
        CreateDiagnosisRequest request = new CreateDiagnosisRequest();
        request.setJournalEntryId(99L);

        when(journalRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> diagnosisService.createDiagnosis(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Journalpost med id: 99 hittades inte");

        verify(diagnosisRepository, never()).save(any());
    }

    // ==========================================
    // Tests for getDiagnosisByPatientId() - the US-32 flow
    // ==========================================

    @Test
    @DisplayName("getDiagnosisByPatientId - ska returnera diagnoser mappade till DTO:er för given patient")
    void getDiagnosisByPatientId_ShouldReturnMappedDtos() {
        // Arrange
        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setId(1L);
        diagnosis.setJournalEntry(journalEntry);
        diagnosis.setName("Astma");

        DiagnosisDTO dto = new DiagnosisDTO(1L, 10L, 5L, "J45.9", "Astma", "Lindrig astma", Date.valueOf("2026-08-20"));

        when(diagnosisRepository.findByJournalEntry_CareContact_PatientId(42L)).thenReturn(List.of(diagnosis));
        when(diagnosisMapper.toDto(diagnosis)).thenReturn(dto);

        // Act
        List<DiagnosisDTO> result = diagnosisService.getDiagnosisByPatientId(42L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getJournalEntryId()).isEqualTo(10L);
        verify(diagnosisRepository, times(1)).findByJournalEntry_CareContact_PatientId(42L);
    }
}
