package com.astral004.appointmentapi.repository;

import com.astral004.appointmentapi.domain.Patient;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PatientRepository extends MongoRepository<Patient, String> {
    List<Patient> getAllByLastName(String lastName);
    List<Patient> getAllByFirstTime(Boolean firstTime);
}
