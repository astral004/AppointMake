package com.astral004.appointmentapi.controller;

import com.astral004.appointmentapi.domain.Appointment;
import com.astral004.appointmentapi.domain.Clinic;
import com.astral004.appointmentapi.domain.Doctor;
import com.astral004.appointmentapi.domain.Patient;
import com.astral004.appointmentapi.exception.AppointmentDoesNotExistException;
import com.astral004.appointmentapi.repository.AppointmentRepository;
import com.astral004.appointmentapi.repository.ClinicRepository;
import com.astral004.appointmentapi.repository.DoctorRepository;
import com.astral004.appointmentapi.repository.PatientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
public class AppointmentControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private AppointmentRepository appointmentRepository;
    @MockBean
    private PatientRepository patientRepository;
    @MockBean
    private DoctorRepository doctorRepository;
    @MockBean
    private ClinicRepository clinicRepository;

    private Appointment appointment;
    private List<Appointment> appointments;
    private ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();

    @Before
    public void setUp() throws Exception {
        appointment = new Appointment();
        appointment.setId("A000001");
        appointment.setPatientId("P000001");
        appointment.setDoctorId("D000001");
        appointment.setClinicId("C000001");
        LocalDate timing = LocalDate.now().plusDays(2);
        appointment.setTiming(timing);
        appointment.setDescription("Patient has pain in the right ankle.");
        appointment.setInsuranceId("I000001");
        appointment.setCopay(20);
        appointment.setStatus("Scheduled");
        appointments = new ArrayList<>();
        appointments.add(appointment);
    }

    @Test
    public void getAllAppointmentsTest() throws Exception {
        Mockito.when(appointmentRepository.findAll()).thenReturn(appointments);
        mockMvc.perform(get("/appointments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllAppointments_AppointmentDoesNotExist_Test() throws Exception {
        List<Appointment> returnedAppointments = new ArrayList<>();
        Mockito.when(appointmentRepository.findAll()).thenReturn(returnedAppointments);
        mockMvc.perform(get("/appointments")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(result -> assertTrue(result.getResolvedException() instanceof AppointmentDoesNotExistException));
    }

    @Test
    public void getAllAppointments_internalServerErrorTest() throws Exception {
        Mockito.when(appointmentRepository.findAll()).thenReturn(null);
        mockMvc.perform(get("/appointments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void getAppointmentsByValueTest() throws Exception {
        Mockito.when(appointmentRepository.findAll()).thenReturn(appointments);
        mockMvc.perform(get("/appointmentsByValue")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.when(appointmentRepository.findAppointmentsByClinicIdAndDoctorIdAndPatientId(
                appointment.getClinicId(),
                appointment.getDoctorId(),
                appointment.getPatientId())).thenReturn(appointments);
        mockMvc.perform(get("/appointmentsByValue")
                .contentType(MediaType.APPLICATION_JSON)
                .param("cID", appointment.getClinicId())
                .param("dID", appointment.getDoctorId())
                .param("pID", appointment.getPatientId()))
                .andExpect(status().isOk());

        Mockito.when(appointmentRepository.findAppointmentsByClinicIdAndDoctorId(appointment.getClinicId(),
                appointment.getDoctorId())).thenReturn(appointments);
        mockMvc.perform(get("/appointmentsByValue")
                .param("cID", appointment.getClinicId())
                .param("dID", appointment.getDoctorId()))
                .andExpect(status().isOk());

        Mockito.when(appointmentRepository.findAppointmentsByClinicIdAndPatientId(appointment.getClinicId(),
                appointment.getPatientId())).thenReturn(appointments);
        mockMvc.perform(get("/appointmentsByValue")
                .param("cID", appointment.getClinicId())
                .param("pID", appointment.getPatientId()))
                .andExpect(status().isOk());

        Mockito.when(appointmentRepository.findAppointmentsByDoctorIdAndPatientId(appointment.getDoctorId(),
                appointment.getPatientId())).thenReturn(appointments);
        mockMvc.perform(get("/appointmentsByValue")
                .param("dID", appointment.getDoctorId())
                .param("pID", appointment.getPatientId()))
                .andExpect(status().isOk());

        Mockito.when(appointmentRepository.findAppointmentsByClinicId(appointment.getClinicId())).thenReturn(appointments);
        mockMvc.perform(get("/appointmentsByValue")
                .param("cID", appointment.getClinicId()))
                .andExpect(status().isOk());

        Mockito.when(appointmentRepository.findAppointmentsByDoctorId(appointment.getDoctorId())).thenReturn(appointments);
        mockMvc.perform(get("/appointmentsByValue")
                .param("dID", appointment.getDoctorId()))
                .andExpect(status().isOk());

        Mockito.when(appointmentRepository.findAppointmentsByPatientId(appointment.getPatientId())).thenReturn(appointments);
        mockMvc.perform(get("/appointmentsByValue")
                .param("pID", appointment.getPatientId()))
                .andExpect(status().isOk());
    }

    @Test
    public void getAppointmentsByValueTestException()throws Exception{
        Mockito.when(appointmentRepository.findAppointmentsByClinicIdAndDoctorIdAndPatientId(
                appointment.getClinicId(),
                appointment.getDoctorId(),
                appointment.getPatientId())).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/appointmentsByValue")
                .contentType(MediaType.APPLICATION_JSON)
                .param("cID", appointment.getClinicId())
                .param("dID", appointment.getDoctorId())
                .param("pID", appointment.getPatientId()))
                .andExpect(status().isNotFound());

        Mockito.when(appointmentRepository.findAppointmentsByClinicIdAndDoctorIdAndPatientId(
                appointment.getClinicId(),
                appointment.getDoctorId(),
                appointment.getPatientId())).thenReturn(null);
        mockMvc.perform(get("/appointmentsByValue")
                .contentType(MediaType.APPLICATION_JSON)
                .param("cID", appointment.getClinicId())
                .param("dID", appointment.getDoctorId())
                .param("pID", appointment.getPatientId()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void getAppointmentsByIDTest() throws Exception {
        Mockito.when(appointmentRepository.findById(Mockito.any())).thenReturn(Optional.of(appointment));
        mockMvc.perform(get("/appointments/{id}", appointment.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getAppointmentsByIDTestException() throws Exception{
        Mockito.when(appointmentRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        mockMvc.perform(get("/appointments/{id}", "test"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createAppointmentTest() throws Exception {
        String jsonAppointment = objectMapper.writeValueAsString(appointment);
        Mockito.when(patientRepository.findById(Mockito.any())).thenReturn(Optional.of(new Patient()));
        Mockito.when(doctorRepository.findById(Mockito.any())).thenReturn(Optional.of(new Doctor()));
        Mockito.when(clinicRepository.findById(Mockito.any())).thenReturn(Optional.of(new Clinic()));
        Mockito.when(appointmentRepository.save(Mockito.any())).thenReturn(appointment);
        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonAppointment))
                .andExpect(status().isCreated());
    }

    @Test
    public void createAppointmentTestException() throws Exception{
        String cleanAppointment = objectMapper.writeValueAsString(appointment);
        appointment.setTiming(LocalDate.now().minusDays(2));
        String badAppointment = objectMapper.writeValueAsString(appointment);
        Mockito.when(patientRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(doctorRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(clinicRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(appointmentRepository.save(Mockito.any())).thenThrow(new NullPointerException("Something was null."));
        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(badAppointment))
                .andExpect(status().isNotAcceptable());

        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cleanAppointment))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createAppointmentTestCatch() throws Exception{
        String json = objectMapper.writeValueAsString(appointment);
        Mockito.when(patientRepository.findById(Mockito.any())).thenReturn(Optional.of(new Patient()));
        Mockito.when(doctorRepository.findById(Mockito.any())).thenReturn(Optional.of(new Doctor()));
        Mockito.when(clinicRepository.findById(Mockito.any())).thenReturn(Optional.of(new Clinic()));
        Mockito.when(appointmentRepository.save(Mockito.any())).thenThrow(new NullPointerException("Something went wrong!"));
        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void updateAppointmentTest()throws Exception{
        appointment.setTiming(appointment.getTiming().plusDays(3));
        String json = objectMapper.writeValueAsString(appointment);
        when(appointmentRepository.findById(any())).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenReturn(appointment);
        mockMvc.perform(put("/appointments/{id}", appointment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void updateAppointmentTestException()throws Exception{
        when(appointmentRepository.findById(any())).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenThrow(new NullPointerException("NULL"));
        appointment.setTiming(appointment.getTiming().plusDays(3));
        String cleanDate = objectMapper.writeValueAsString(appointment);
        mockMvc.perform(put("/appointments/{id}", appointment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(cleanDate))
                .andExpect(status().isInternalServerError());

        appointment.setTiming(LocalDate.now().plusDays(1));
        String badDate = objectMapper.writeValueAsString(appointment);
        mockMvc.perform(put("/appointments/{id}", appointment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(badDate))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void updateAppointmentTestNotFound()throws Exception{
        when(appointmentRepository.findById(any())).thenReturn(Optional.empty());
        mockMvc.perform(put("/appointments/{id}", appointment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointment)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteAppointmentByIDTest()throws Exception{
        appointment.setTiming(appointment.getTiming().plusDays(3));
        String json = objectMapper.writeValueAsString(appointment);
        when(appointmentRepository.findById(any())).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenReturn(appointment);
        mockMvc.perform(delete("/appointments/{id}", appointment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteAppointmentByIDTestException()throws Exception{
        when(appointmentRepository.findById(any())).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenThrow(new NullPointerException("NULL"));
        appointment.setTiming(appointment.getTiming().plusDays(3));
        String cleanDate = objectMapper.writeValueAsString(appointment);
        mockMvc.perform(delete("/appointments/{id}", appointment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(cleanDate))
                .andExpect(status().isInternalServerError());

        appointment.setTiming(LocalDate.now().plusDays(1));
        String badDate = objectMapper.writeValueAsString(appointment);
        mockMvc.perform(delete("/appointments/{id}", appointment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(badDate))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void deleteAppointmentByIDTestNotFound()throws Exception{
        when(appointmentRepository.findById(any())).thenReturn(Optional.empty());
        mockMvc.perform(delete("/appointments/{id}", appointment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointment)))
                .andExpect(status().isNotFound());
    }

}