package com.astral004.appointmentapi.repository;

import com.astral004.appointmentapi.domain.Doctor;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DoctorRepository extends MongoRepository<Doctor, String> {
    List<Doctor> getDoctorsBySpecialty(String Specialty);
}
