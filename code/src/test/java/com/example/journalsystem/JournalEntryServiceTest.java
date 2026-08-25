package com.example.journalsystem;

import com.example.journalsystem.dto.CreateJournalEntryRequest;
import com.example.journalsystem.entities.CareContact;
import com.example.journalsystem.entities.JournalEntry;
import com.example.journalsystem.entities.JournalEntryDTO;
import com.example.journalsystem.entities.JournalEntryMapper;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.repository.CareContactRepository;
import com.example.journalsystem.repository.JournalRepository;
import com.example.journalsystem.repository.StaffRepository;
import com.example.journalsystem.service.AuditLogService;
import com.example.journalsystem.service.JournalEntryService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/// US-13 — Som läkare vill jag kunna skapa en journalpost av typen
/// ANTECKNING, UNDERSÖKNING eller OPERATION kopplad till en vårdkontakt.
@ExtendWith(MockitoExtension.class)
class JournalEntryServiceTest {

    @Mock
    private JournalRepository journalRepository;

    @Mock
    private JournalEntryMapper journalEntryMapper;

    @Mock
    private CareContactRepository careContactRepository;

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private HttpServletRequest httpRequest;

    @InjectMocks
    private JournalEntryService journalEntryService;

    @Captor
    private ArgumentCaptor<JournalEntry> journalEntryCaptor;

    private CareContact careContact;
    private CreateJournalEntryRequest request;
    private JournalEntry savedEntry;
    private JournalEntryDTO entryDTO;

    @BeforeEach
    void setUp() {
        careContact = new CareContact();
        careContact.setId(5L);
        careContact.setPatientId(1L);

        request = new CreateJournalEntryRequest(5L, 3L,
                JournalEntryService.TYPE_NOTE, "Patienten klagar på bröstsmärta.");

        savedEntry = new JournalEntry();
        savedEntry.setId(20L);
        savedEntry.setCareContact(careContact);
        savedEntry.setCreatedBy(3L);
        savedEntry.setType(JournalEntryService.TYPE_NOTE);
        savedEntry.setContent("Patienten klagar på bröstsmärta.");

        entryDTO = new JournalEntryDTO(20L,
                Timestamp.valueOf("2026-08-23 09:00:00"),
                JournalEntryService.TYPE_NOTE,
                "Patienten klagar på bröstsmärta.");
    }

    // ==========================================
    // Tests for createJournalEntry()
    // ==========================================

    @Test
    @DisplayName("createJournalEntry - ska spara och returnera journalpost när vårdkontakt och personal finns")
    void createJournalEntry_WhenReferencesExist_ShouldSaveAndReturnDto() {
        // Arrange
        when(careContactRepository.findById(5L)).thenReturn(Optional.of(careContact));
        when(staffRepository.existsById(3L)).thenReturn(true);
        when(journalRepository.save(any(JournalEntry.class))).thenReturn(savedEntry);
        when(journalEntryMapper.toDto(savedEntry)).thenReturn(entryDTO);

        // Act
        JournalEntryDTO result = journalEntryService.createJournalEntry(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(20L);
        assertThat(result.getType()).isEqualTo(JournalEntryService.TYPE_NOTE);
        verify(journalRepository, times(1)).save(any(JournalEntry.class));
    }

    @Test
    @DisplayName("createJournalEntry - ska koppla posten till vårdkontakten och sätta createdAt automatiskt")
    void createJournalEntry_ShouldLinkCareContactAndSetCreatedAt() {
        // Arrange
        when(careContactRepository.findById(5L)).thenReturn(Optional.of(careContact));
        when(staffRepository.existsById(3L)).thenReturn(true);
        when(journalRepository.save(any(JournalEntry.class))).thenReturn(savedEntry);
        when(journalEntryMapper.toDto(savedEntry)).thenReturn(entryDTO);

        // Act
        journalEntryService.createJournalEntry(request);

        // Assert
        verify(journalRepository).save(journalEntryCaptor.capture());
        JournalEntry captured = journalEntryCaptor.getValue();

        assertThat(captured.getCareContact()).isSameAs(careContact);
        assertThat(captured.getCreatedBy()).isEqualTo(3L);
        assertThat(captured.getType()).isEqualTo(JournalEntryService.TYPE_NOTE);
        assertThat(captured.getContent()).isEqualTo("Patienten klagar på bröstsmärta.");
        assertThat(captured.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("createJournalEntry - ska spara typen examination när den efterfrågas")
    void createJournalEntry_WithExaminationType_ShouldSaveThatType() {
        // Arrange
        CreateJournalEntryRequest examRequest = new CreateJournalEntryRequest(
                5L, 3L, JournalEntryService.TYPE_EXAMINATION, "EKG utfört.");

        when(careContactRepository.findById(5L)).thenReturn(Optional.of(careContact));
        when(staffRepository.existsById(3L)).thenReturn(true);
        when(journalRepository.save(any(JournalEntry.class))).thenReturn(savedEntry);
        when(journalEntryMapper.toDto(savedEntry)).thenReturn(entryDTO);

        // Act
        journalEntryService.createJournalEntry(examRequest);

        // Assert
        verify(journalRepository).save(journalEntryCaptor.capture());
        assertThat(journalEntryCaptor.getValue().getType())
                .isEqualTo(JournalEntryService.TYPE_EXAMINATION);
    }

    @Test
    @DisplayName("createJournalEntry - ska kasta ResourceNotFoundException när vårdkontakten inte finns")
    void createJournalEntry_WhenCareContactDoesNotExist_ShouldThrowException() {
        // Arrange
        when(careContactRepository.findById(5L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> journalEntryService.createJournalEntry(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Vårdkontakt med id: 5 hittades inte");

        verify(journalRepository, never()).save(any());
    }

    @Test
    @DisplayName("createJournalEntry - ska kasta ResourceNotFoundException när personalen inte finns")
    void createJournalEntry_WhenStaffDoesNotExist_ShouldThrowException() {
        // Arrange
        when(careContactRepository.findById(5L)).thenReturn(Optional.of(careContact));
        when(staffRepository.existsById(3L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> journalEntryService.createJournalEntry(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Personal med id: 3 hittades inte");

        verify(journalRepository, never()).save(any());
        verifyNoInteractions(journalEntryMapper);
    }
}
