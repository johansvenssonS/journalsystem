package com.example.journalsystem;

import com.example.journalsystem.dto.CreateJournalEntryRequest;
import com.example.journalsystem.entities.CareContact;
import com.example.journalsystem.entities.JournalEntry;
import com.example.journalsystem.entities.JournalEntryDTO;
import com.example.journalsystem.entities.JournalEntryMapper;
import com.example.journalsystem.repository.CareContactRepository;
import com.example.journalsystem.repository.JournalRepository;
import com.example.journalsystem.repository.StaffRepository;
import com.example.journalsystem.service.AuditLogService;
import com.example.journalsystem.service.JournalEntryService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/// US-22 — Som sjuksköterska vill jag kunna skapa en journalpost av typen
/// ANTECKNING så att omvårdnadsåtgärder dokumenteras. Sjuksköterskan ska
/// däremot inte kunna skapa undersökningar eller operationer.
@ExtendWith(MockitoExtension.class)
class JournalEntryNurseRulesTest {

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
    private JournalEntry savedEntry;
    private JournalEntryDTO entryDTO;

    @BeforeEach
    void setUp() {
        careContact = new CareContact();
        careContact.setId(5L);

        savedEntry = new JournalEntry();
        savedEntry.setId(30L);
        savedEntry.setCareContact(careContact);

        entryDTO = new JournalEntryDTO(30L,
                Timestamp.valueOf("2026-08-23 10:00:00"),
                JournalEntryService.TYPE_NOTE,
                "Patienten har fått sin medicin.");
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void loginAs(String authority) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "testanvandare",
                        "n/a",
                        List.of(new SimpleGrantedAuthority(authority))));
    }

    private CreateJournalEntryRequest requestOfType(String type) {
        return new CreateJournalEntryRequest(5L, 3L, type, "Omvårdnadsanteckning.");
    }

    // ==========================================
    // Sjuksköterska
    // ==========================================

    @Test
    @DisplayName("US-22 sjuksköterska - ska få skapa journalpost av typen note")
    void nurse_CreatingNote_ShouldBeAllowed() {
        // Arrange
        loginAs(JournalEntryService.ROLE_NURSE);
        when(careContactRepository.findById(5L)).thenReturn(Optional.of(careContact));
        when(staffRepository.existsById(3L)).thenReturn(true);
        when(journalRepository.save(any(JournalEntry.class))).thenReturn(savedEntry);
        when(journalEntryMapper.toDto(savedEntry)).thenReturn(entryDTO);

        // Act
        JournalEntryDTO result = journalEntryService
                .createJournalEntry(requestOfType(JournalEntryService.TYPE_NOTE));

        // Assert
        assertThat(result).isNotNull();
        verify(journalRepository).save(journalEntryCaptor.capture());
        assertThat(journalEntryCaptor.getValue().getType())
                .isEqualTo(JournalEntryService.TYPE_NOTE);
    }

    @Test
    @DisplayName("US-22 sjuksköterska - ska nekas att skapa journalpost av typen examination")
    void nurse_CreatingExamination_ShouldBeDenied() {
        // Arrange
        loginAs(JournalEntryService.ROLE_NURSE);

        // Act & Assert
        assertThatThrownBy(() -> journalEntryService
                .createJournalEntry(requestOfType(JournalEntryService.TYPE_EXAMINATION)))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Sjuksköterska får endast skapa journalposter av typen note");

        verify(journalRepository, never()).save(any());
        verifyNoInteractions(careContactRepository);
    }

    @Test
    @DisplayName("US-22 sjuksköterska - ska nekas att skapa journalpost av typen operation")
    void nurse_CreatingOperation_ShouldBeDenied() {
        // Arrange
        loginAs(JournalEntryService.ROLE_NURSE);

        // Act & Assert
        assertThatThrownBy(() -> journalEntryService
                .createJournalEntry(requestOfType(JournalEntryService.TYPE_OPERATION)))
                .isInstanceOf(AccessDeniedException.class);

        verify(journalRepository, never()).save(any());
    }

    // ==========================================
    // Läkare - regressionsskydd, US-13 ska inte påverkas
    // ==========================================

    @Test
    @DisplayName("US-13 läkare - ska fortfarande få skapa journalpost av typen operation")
    void doctor_CreatingOperation_ShouldStillBeAllowed() {
        // Arrange
        loginAs(JournalEntryService.ROLE_DOCTOR);
        when(careContactRepository.findById(5L)).thenReturn(Optional.of(careContact));
        when(staffRepository.existsById(3L)).thenReturn(true);
        when(journalRepository.save(any(JournalEntry.class))).thenReturn(savedEntry);
        when(journalEntryMapper.toDto(savedEntry)).thenReturn(entryDTO);

        // Act
        journalEntryService.createJournalEntry(requestOfType(JournalEntryService.TYPE_OPERATION));

        // Assert
        verify(journalRepository).save(journalEntryCaptor.capture());
        assertThat(journalEntryCaptor.getValue().getType())
                .isEqualTo(JournalEntryService.TYPE_OPERATION);
    }
}
