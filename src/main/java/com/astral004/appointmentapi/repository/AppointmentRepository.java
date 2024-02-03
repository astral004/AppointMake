package com.astral004.appointmentapi.repository;

import com.astral004.appointmentapi.domain.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AppointmentRepository extends MongoRepository<Appointment, String> {

    List<Appointment> findAppointmentsByClinicId(String clinic_id);

    List<Appointment> findAppointmentsByClinicIdAndPatientId(String clinic_id, String patient_id);

    List<Appointment> findAppointmentsByClinicIdAndDoctorId(String clinic_id, String doctor_id);

    List<Appointment> findAppointmentsByDoctorId(String doctor_id);
    List<Appointment> findAppointmentsByDoctorIdAndPatientId(String doctor_id, String patient_id);

    List<Appointment> findAppointmentsByPatientId(String patient_id);

    List<Appointment> findAppointmentsByClinicIdAndDoctorIdAndPatientId(String clinic_id, String doctor_id, String patient_id);


}
