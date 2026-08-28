package com.example.journalsystem;

import com.example.journalsystem.dto.DischargeCareContactRequest;
import com.example.journalsystem.entities.CareContact;
import com.example.journalsystem.entities.CareContactDTO;
import com.example.journalsystem.entities.CareContactMapper;
import com.example.journalsystem.exceptions.DuplicateResourceException;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.repository.CareContactRepository;
import com.example.journalsystem.repository.DepartmentRepository;
import com.example.journalsystem.repository.PatientRepository;
import com.example.journalsystem.repository.StaffRepository;
import com.example.journalsystem.service.CareContactService;
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
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/// US-17 — Som läkare vill jag kunna skriva ut en patient
/// (sätta status UTSKRIVEN på vårdkontakten) med ett utskrivningsdatum.
@ExtendWith(MockitoExtension.class)
class CareContactDischargeTest {

    @Mock
    private CareContactRepository careContactRepository;

    @Mock
    private CareContactMapper careContactMapper;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private StaffRepository staffRepository;

    @InjectMocks
    private CareContactService careContactService;

    @Captor
    private ArgumentCaptor<CareContact> careContactCaptor;

    private CareContact admittedContact;
    private CareContactDTO dischargedDTO;

    @BeforeEach
    void setUp() {
        admittedContact = new CareContact();
        admittedContact.setId(10L);
        admittedContact.setPatientId(1L);
        admittedContact.setDepartmentId(2L);
        admittedContact.setResponsibleStaffId(3L);
        admittedContact.setReason("Bröstsmärta");
        admittedContact.setAdmitDate(Timestamp.valueOf("2026-08-20 08:00:00"));
        admittedContact.setStatus(CareContactService.STATUS_ADMITTED);

        dischargedDTO = new CareContactDTO(
                10L, 2L, 3L, "Bröstsmärta",
                Timestamp.valueOf("2026-08-20 08:00:00"),
                CareContactService.STATUS_DISCHARGED);
    }

    // ==========================================
    // Tests for dischargeCareContact()
    // ==========================================

    @Test
    @DisplayName("dischargeCareContact - ska sätta status till discharged och fylla i dischargeDate")
    void discharge_WhenContactIsAdmitted_ShouldSetStatusAndDate() {
        // Arrange
        when(careContactRepository.findById(10L)).thenReturn(Optional.of(admittedContact));
        when(careContactRepository.save(any(CareContact.class))).thenReturn(admittedContact);
        when(careContactMapper.toDto(admittedContact)).thenReturn(dischargedDTO);

        // Act
        CareContactDTO result = careContactService.dischargeCareContact(10L, null);

        // Assert
        assertThat(result.getStatus()).isEqualTo(CareContactService.STATUS_DISCHARGED);

        verify(careContactRepository).save(careContactCaptor.capture());
        CareContact captured = careContactCaptor.getValue();
        assertThat(captured.getStatus()).isEqualTo(CareContactService.STATUS_DISCHARGED);
        assertThat(captured.getDischargeDate()).isNotNull();
    }

    @Test
    @DisplayName("dischargeCareContact - ska använda angivet utskrivningsdatum när det skickas med")
    void discharge_WithGivenDate_ShouldUseThatDate() {
        // Arrange
        LocalDateTime chosen = LocalDateTime.of(2026, 8, 22, 14, 30, 0);
        DischargeCareContactRequest request = new DischargeCareContactRequest(chosen);

        when(careContactRepository.findById(10L)).thenReturn(Optional.of(admittedContact));
        when(careContactRepository.save(any(CareContact.class))).thenReturn(admittedContact);
        when(careContactMapper.toDto(admittedContact)).thenReturn(dischargedDTO);

        // Act
        careContactService.dischargeCareContact(10L, request);

        // Assert
        verify(careContactRepository).save(careContactCaptor.capture());
        assertThat(careContactCaptor.getValue().getDischargeDate())
                .isEqualTo(Timestamp.valueOf(chosen));
    }

    @Test
    @DisplayName("dischargeCareContact - ska kasta ResourceNotFoundException när vårdkontakten inte finns")
    void discharge_WhenContactDoesNotExist_ShouldThrowException() {
        // Arrange
        when(careContactRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> careContactService.dischargeCareContact(99L, null))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Vårdkontakt med id: 99 hittades inte");

        verify(careContactRepository, never()).save(any());
    }

    @Test
    @DisplayName("dischargeCareContact - ska kasta DuplicateResourceException när patienten redan är utskriven")
    void discharge_WhenAlreadyDischarged_ShouldThrowException() {
        // Arrange
        admittedContact.setStatus(CareContactService.STATUS_DISCHARGED);
        when(careContactRepository.findById(10L)).thenReturn(Optional.of(admittedContact));

        // Act & Assert
        assertThatThrownBy(() -> careContactService.dischargeCareContact(10L, null))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Vårdkontakt med id: 10 är redan utskriven");

        verify(careContactRepository, never()).save(any());
        verifyNoInteractions(careContactMapper);
    }
}
