package com.example.journalsystem;

import com.example.journalsystem.entities.CareContact;
import com.example.journalsystem.repository.CareContactRepository;
import com.example.journalsystem.service.CurrentUserService;
import com.example.journalsystem.service.PatientAccessService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/// US-2 och US-51 — avdelningsspärren.
///
/// Regeln: personal får se en patients data om patienten har en aktiv
/// vårdkontakt på personalens avdelning.
@ExtendWith(MockitoExtension.class)
class PatientAccessServiceTest {

    private static final String DOCTOR = "ROLE_doctor";
    private static final String NURSE = "ROLE_nurse";
    private static final String ASSISTANT = "ROLE_assistant_nurse";

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private CareContactRepository careContactRepository;

    @InjectMocks
    private PatientAccessService patientAccessService;

    /// Stubbar hela hasAuthority i stället för en enskild roll. isClinicalUser
    /// itererar över flera roller och avbryter vid första träffen, så en stubbe
    /// per roll gör testet beroende av iterationsordningen.
    private void loggedInAs(String authority) {
        when(currentUserService.hasAuthority(anyString()))
                .thenAnswer(invocation -> authority.equals(invocation.getArgument(0)));
    }

    private void loggedInAsDoctorOnDepartment(Long departmentId) {
        loggedInAs(DOCTOR);
        when(currentUserService.getDepartmentId()).thenReturn(Optional.ofNullable(departmentId));
    }

    // ==========================================
    // canAccessPatient()
    // ==========================================

    @Test
    @DisplayName("US-2 - läkare ska få se patient som är inskriven på hens egen avdelning")
    void clinicalUser_PatientAdmittedOnOwnDepartment_ShouldBeAllowed() {
        // Arrange
        loggedInAsDoctorOnDepartment(1L);
        when(careContactRepository.existsByPatientIdAndDepartmentIdAndStatus(
                5L, 1L, PatientAccessService.STATUS_ADMITTED)).thenReturn(true);

        // Act & Assert
        assertThat(patientAccessService.canAccessPatient(5L)).isTrue();
        assertThatCode(() -> patientAccessService.assertCanAccessPatient(5L))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("US-51 - läkare ska nekas patient utan aktiv vårdkontakt på hens avdelning")
    void clinicalUser_PatientOnOtherDepartment_ShouldBeDenied() {
        // Arrange
        loggedInAsDoctorOnDepartment(1L);
        when(careContactRepository.existsByPatientIdAndDepartmentIdAndStatus(
                9L, 1L, PatientAccessService.STATUS_ADMITTED)).thenReturn(false);

        // Act & Assert
        assertThat(patientAccessService.canAccessPatient(9L)).isFalse();
        assertThatThrownBy(() -> patientAccessService.assertCanAccessPatient(9L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("inte inskriven på din avdelning");
    }

    @Test
    @DisplayName("US-51 - vårdpersonal utan anställning ska nekas")
    void clinicalUser_WithoutEmployment_ShouldBeDenied() {
        // Arrange
        loggedInAs(DOCTOR);
        when(currentUserService.getDepartmentId()).thenReturn(Optional.empty());

        // Act & Assert
        assertThat(patientAccessService.canAccessPatient(5L)).isFalse();
        verifyNoInteractions(careContactRepository);
    }

    @Test
    @DisplayName("US-2 - receptionisten omfattas inte av avdelningsspärren")
    void nonClinicalUser_ShouldBeAllowed() {
        // Arrange
        loggedInAs("ROLE_RECEPTIONIST");

        // Act & Assert
        assertThat(patientAccessService.canAccessPatient(5L)).isTrue();
        verifyNoInteractions(careContactRepository);
    }

    @Test
    @DisplayName("canAccessPatient - null som patientId ska nekas")
    void nullPatientId_ShouldBeDenied() {
        assertThat(patientAccessService.canAccessPatient(null)).isFalse();
        verifyNoInteractions(careContactRepository);
    }

    @Test
    @DisplayName("US-2 - sjuksköterska omfattas av samma avdelningsspärr som läkare")
    void nurse_PatientOnOtherDepartment_ShouldBeDenied() {
        // Arrange
        loggedInAs(NURSE);
        when(currentUserService.getDepartmentId()).thenReturn(Optional.of(3L));
        when(careContactRepository.existsByPatientIdAndDepartmentIdAndStatus(
                4L, 3L, PatientAccessService.STATUS_ADMITTED)).thenReturn(false);

        // Act & Assert
        assertThat(patientAccessService.canAccessPatient(4L)).isFalse();
    }

    @Test
    @DisplayName("US-2 - undersköterska omfattas av samma avdelningsspärr")
    void assistantNurse_PatientOnOwnDepartment_ShouldBeAllowed() {
        // Arrange
        loggedInAs(ASSISTANT);
        when(currentUserService.getDepartmentId()).thenReturn(Optional.of(3L));
        when(careContactRepository.existsByPatientIdAndDepartmentIdAndStatus(
                4L, 3L, PatientAccessService.STATUS_ADMITTED)).thenReturn(true);

        // Act & Assert
        assertThat(patientAccessService.canAccessPatient(4L)).isTrue();
    }

    // ==========================================
    // accessiblePatientIds()
    // ==========================================

    @Test
    @DisplayName("accessiblePatientIds - ska returnera unika patient-id från avdelningens vårdkontakter")
    void accessiblePatientIds_ShouldReturnDistinctIds() {
        // Arrange
        when(currentUserService.getDepartmentId()).thenReturn(Optional.of(2L));
        when(careContactRepository.findByDepartmentIdAndStatus(
                2L, PatientAccessService.STATUS_ADMITTED))
                .thenReturn(List.of(careContactFor(7L), careContactFor(8L), careContactFor(7L)));

        // Act
        List<Long> result = patientAccessService.accessiblePatientIds();

        // Assert
        assertThat(result).containsExactly(7L, 8L);
    }

    @Test
    @DisplayName("accessiblePatientIds - ska returnera tom lista när anställning saknas")
    void accessiblePatientIds_WithoutEmployment_ShouldReturnEmpty() {
        when(currentUserService.getDepartmentId()).thenReturn(Optional.empty());

        assertThat(patientAccessService.accessiblePatientIds()).isEmpty();
        verifyNoInteractions(careContactRepository);
    }

    private CareContact careContactFor(long patientId) {
        CareContact careContact = new CareContact();
        careContact.setPatientId(patientId);
        return careContact;
    }
}
