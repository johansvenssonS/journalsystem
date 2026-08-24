package com.example.journalsystem;

import com.example.journalsystem.dto.CreateCareContactRequest;
import com.example.journalsystem.entities.CareContact;
import com.example.journalsystem.entities.CareContactDTO;
import com.example.journalsystem.entities.CareContactMapper;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/// US-12 — Som läkare vill jag kunna skapa en ny vårdkontakt för en patient på min avdelning.
@ExtendWith(MockitoExtension.class)
class CareContactServiceTest {

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

    private CreateCareContactRequest request;
    private CareContact savedCareContact;
    private CareContactDTO careContactDTO;

    @BeforeEach
    void setUp() {
        request = new CreateCareContactRequest(1L, 2L, 3L, "Bröstsmärta");

        savedCareContact = new CareContact();
        savedCareContact.setId(10L);
        savedCareContact.setPatientId(1L);
        savedCareContact.setDepartmentId(2L);
        savedCareContact.setResponsibleStaffId(3L);
        savedCareContact.setReason("Bröstsmärta");
        savedCareContact.setStatus(CareContactService.STATUS_ADMITTED);

        careContactDTO = new CareContactDTO(
                10L, 2L, 3L, "Bröstsmärta",
                Timestamp.valueOf("2026-08-22 10:00:00"),
                CareContactService.STATUS_ADMITTED);
    }

    // ==========================================
    // Tests for createCareContact()
    // ==========================================

    @Test
    @DisplayName("createCareContact - ska spara och returnera vårdkontakt när patient, avdelning och personal finns")
    void createCareContact_WhenAllReferencesExist_ShouldSaveAndReturnDto() {
        // Arrange
        when(patientRepository.existsById(1L)).thenReturn(true);
        when(departmentRepository.existsById(2L)).thenReturn(true);
        when(staffRepository.existsById(3L)).thenReturn(true);
        when(careContactRepository.save(any(CareContact.class))).thenReturn(savedCareContact);
        when(careContactMapper.toDto(savedCareContact)).thenReturn(careContactDTO);

        // Act
        CareContactDTO result = careContactService.createCareContact(request);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getReason()).isEqualTo("Bröstsmärta");
        verify(careContactRepository, times(1)).save(any(CareContact.class));
    }

    @Test
    @DisplayName("createCareContact - ska sätta status till admitted och fylla i admitDate automatiskt")
    void createCareContact_ShouldSetStatusAdmittedAndAdmitDate() {
        // Arrange
        when(patientRepository.existsById(1L)).thenReturn(true);
        when(departmentRepository.existsById(2L)).thenReturn(true);
        when(staffRepository.existsById(3L)).thenReturn(true);
        when(careContactRepository.save(any(CareContact.class))).thenReturn(savedCareContact);
        when(careContactMapper.toDto(savedCareContact)).thenReturn(careContactDTO);

        // Act
        careContactService.createCareContact(request);

        // Assert
        verify(careContactRepository).save(careContactCaptor.capture());
        CareContact captured = careContactCaptor.getValue();

        assertThat(captured.getStatus()).isEqualTo(CareContactService.STATUS_ADMITTED);
        assertThat(captured.getAdmitDate()).isNotNull();
        assertThat(captured.getDischargeDate()).isNull();
        assertThat(captured.getPatientId()).isEqualTo(1L);
        assertThat(captured.getDepartmentId()).isEqualTo(2L);
        assertThat(captured.getResponsibleStaffId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("createCareContact - ska kasta ResourceNotFoundException när patienten inte finns")
    void createCareContact_WhenPatientDoesNotExist_ShouldThrowException() {
        // Arrange
        when(patientRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> careContactService.createCareContact(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Patient med id: 1 hittades inte");

        verify(careContactRepository, never()).save(any());
    }

    @Test
    @DisplayName("createCareContact - ska kasta ResourceNotFoundException när avdelningen inte finns")
    void createCareContact_WhenDepartmentDoesNotExist_ShouldThrowException() {
        // Arrange
        when(patientRepository.existsById(1L)).thenReturn(true);
        when(departmentRepository.existsById(2L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> careContactService.createCareContact(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Avdelning med id: 2 hittades inte");

        verify(careContactRepository, never()).save(any());
    }

    @Test
    @DisplayName("createCareContact - ska kasta ResourceNotFoundException när personalen inte finns")
    void createCareContact_WhenStaffDoesNotExist_ShouldThrowException() {
        // Arrange
        when(patientRepository.existsById(1L)).thenReturn(true);
        when(departmentRepository.existsById(2L)).thenReturn(true);
        when(staffRepository.existsById(3L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> careContactService.createCareContact(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Personal med id: 3 hittades inte");

        verify(careContactRepository, never()).save(any());
        verifyNoInteractions(careContactMapper);
    }
}
