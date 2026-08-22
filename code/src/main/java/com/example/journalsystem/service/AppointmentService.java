package com.example.journalsystem.service;

import com.example.journalsystem.dto.AppointmentDto;
import com.example.journalsystem.dto.AppointmentResponse;
import com.example.journalsystem.dto.CreateAppointmentRequest;
import com.example.journalsystem.entities.Appointment;
import com.example.journalsystem.entities.Department;
import com.example.journalsystem.entities.Patient;
import com.example.journalsystem.entities.Staff;
import com.example.journalsystem.exceptions.ResourceNotFoundException;
import com.example.journalsystem.mapper.AppointmentMapper;
import com.example.journalsystem.repository.AppointmentRepository;
import com.example.journalsystem.repository.DepartmentRepository;
import com.example.journalsystem.repository.PatientRepository;
import com.example.journalsystem.repository.StaffRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final PatientRepository patientRepository;
    private final StaffRepository staffRepository;
    private final DepartmentRepository departmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, AppointmentMapper appointmentMapper,
                               PatientRepository patientRepository, StaffRepository staffRepository,
                               DepartmentRepository departmentRepository) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentMapper = appointmentMapper;
        this.patientRepository = patientRepository;
        this.staffRepository = staffRepository;
        this.departmentRepository = departmentRepository;
    }

    public List<AppointmentDto> getAll() {
        return appointmentRepository.findAll().stream()
                .map(appointmentMapper::toDto)
                .toList();
    }

    public AppointmentDto getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bokning med id: " + id + " hittades inte"));
        return appointmentMapper.toDto(appointment);
    }

    public List<AppointmentDto> getByStaffIdAndDate(Long staffId, String date) {
        LocalDate requestedDate = LocalDate.parse(date); // YYYY-MM-DD

        return getAll().stream()
                .filter(a -> a.getStaffId() != null && a.getStaffId().equals(staffId))
                .filter(a -> a.getScheduledAt() != null)
                .filter(a -> a.getScheduledAt()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .equals(requestedDate))
                .sorted(Comparator.comparing(AppointmentDto::getScheduledAt))
                .toList();
    }

    public AppointmentResponse createAppointment(CreateAppointmentRequest createAppointmentRequest) {
        Long patientId = createAppointmentRequest.getPatientId();
        Long staffId = createAppointmentRequest.getStaffId();
        Long departmentId = createAppointmentRequest.getDepartmentId();

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient med id: " + patientId + " hittades inte"));

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Personal med id: " + staffId + " hittades inte"));

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Avdelning med id: " + departmentId + " hittades inte"));

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setStaff(staff);
        appointment.setDepartment(department);
        appointment.setScheduledAt(createAppointmentRequest.getScheduledAt());
        appointment.setNote(createAppointmentRequest.getNote());
        appointment.setStatus(createAppointmentRequest.getStatus() != null
                ? createAppointmentRequest.getStatus()
                : "scheduled");
        appointment.setUpdatedAt(Instant.now());

        Appointment saved = appointmentRepository.save(appointment);

        return new AppointmentResponse(
                saved.getId(),
                saved.getPatient().getId(),
                saved.getStaff().getId(),
                saved.getDepartment().getId(),
                saved.getScheduledAt(),
                saved.getNote(),
                saved.getStatus(),
                saved.getUpdatedAt()
        );
    }
}

