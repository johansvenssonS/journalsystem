package com.example.journalsystem;

import com.example.journalsystem.dto.DepartmentPatientDTO;
import com.example.journalsystem.entities.CareContact;
import com.example.journalsystem.entities.Patient;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.mapper.StaffMapper;
import com.example.journalsystem.repository.CareContactRepository;
import com.example.journalsystem.repository.PatientRepository;
import com.example.journalsystem.repository.StaffRepository;
import com.example.journalsystem.service.StaffService;
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

/// Som läkare/sjuksköterska vill jag snabbt kunna se mina egna patienter,
/// dvs. patienter där jag står som ansvarig personal på en vårdkontakt,
/// så att jag snabbt kan öppna deras journal.
@ExtendWith(MockitoExtension.class)
class StaffAssignedPatientsTest {

    @Mock
    private StaffRepository staffRepository;

    @Mock
    private StaffMapper staffMapper;

    @Mock
    private CareContactRepository careContactRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private StaffService staffService;

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setPersonalNumber("194506127890");
        patient.setFirstName("Sven");
        patient.setLastName("Gustafsson");
    }

    private CareContact careContact(long id, long patientId, String reason, String admitDate) {
        CareContact careContact = new CareContact();
        careContact.setId(id);
        careContact.setPatientId(patientId);
        careContact.setResponsibleStaffId(5L);
        careContact.setReason(reason);
        careContact.setAdmitDate(Timestamp.valueOf(admitDate));
        careContact.setStatus("admitted");
        return careContact;
    }

    @Test
    @DisplayName("getAssignedPatients - ska returnera patienter där personalen är ansvarig")
    void getAssignedPatients_WhenResponsibleForContacts_ShouldReturnPatients() {
        CareContact contact = careContact(10L, 1L, "Bröstsmärta", "2026-08-20 08:00:00");

        when(staffRepository.existsById(5L)).thenReturn(true);
        when(careContactRepository.findByResponsibleStaffId(5L)).thenReturn(List.of(contact));
        when(patientRepository.findAllById(any())).thenReturn(List.of(patient));

        List<DepartmentPatientDTO> result = staffService.getAssignedPatients(5L);

        assertThat(result).hasSize(1);
        DepartmentPatientDTO dto = result.get(0);
        assertThat(dto.getPatientId()).isEqualTo(1L);
        assertThat(dto.getFirstName()).isEqualTo("Sven");
        assertThat(dto.getCareContactId()).isEqualTo(10L);
        assertThat(dto.getReason()).isEqualTo("Bröstsmärta");
    }

    @Test
    @DisplayName("getAssignedPatients - ska bara returnera en rad per patient, från senaste vårdkontakten")
    void getAssignedPatients_WhenMultipleContactsForSamePatient_ShouldReturnOnlyLatest() {
        CareContact older = careContact(10L, 1L, "Äldre besök", "2026-01-10 08:00:00");
        CareContact newer = careContact(11L, 1L, "Återbesök", "2026-08-20 08:00:00");

        when(staffRepository.existsById(5L)).thenReturn(true);
        when(careContactRepository.findByResponsibleStaffId(5L)).thenReturn(List.of(older, newer));
        when(patientRepository.findAllById(any())).thenReturn(List.of(patient));

        List<DepartmentPatientDTO> result = staffService.getAssignedPatients(5L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCareContactId()).isEqualTo(11L);
        assertThat(result.get(0).getReason()).isEqualTo("Återbesök");
    }

    @Test
    @DisplayName("getAssignedPatients - ska returnera tom lista när personalen inte är ansvarig för någon")
    void getAssignedPatients_WhenNoContacts_ShouldReturnEmptyList() {
        when(staffRepository.existsById(5L)).thenReturn(true);
        when(careContactRepository.findByResponsibleStaffId(5L)).thenReturn(List.of());

        List<DepartmentPatientDTO> result = staffService.getAssignedPatients(5L);

        assertThat(result).isEmpty();
        verify(patientRepository, never()).findAllById(any());
    }

    @Test
    @DisplayName("getAssignedPatients - ska kasta ResourceNotFoundException när personalen inte finns")
    void getAssignedPatients_WhenStaffDoesNotExist_ShouldThrowException() {
        when(staffRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> staffService.getAssignedPatients(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Personal med id: 99 hittades inte");

        verifyNoInteractions(careContactRepository);
    }

    @Test
    @DisplayName("getAssignedPatients - ska utelämna patienter som är mjukraderade")
    void getAssignedPatients_WhenPatientIsSoftDeleted_ShouldSkipThem() {
        patient.setDeletedAt(Instant.parse("2026-08-01T00:00:00Z"));
        CareContact contact = careContact(10L, 1L, "Bröstsmärta", "2026-08-20 08:00:00");

        when(staffRepository.existsById(5L)).thenReturn(true);
        when(careContactRepository.findByResponsibleStaffId(5L)).thenReturn(List.of(contact));
        when(patientRepository.findAllById(any())).thenReturn(List.of(patient));

        List<DepartmentPatientDTO> result = staffService.getAssignedPatients(5L);

        assertThat(result).isEmpty();
    }
}
