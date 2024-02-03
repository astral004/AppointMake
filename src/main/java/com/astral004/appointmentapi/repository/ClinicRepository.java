package com.astral004.appointmentapi.repository;

import com.astral004.appointmentapi.domain.Clinic;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClinicRepository extends MongoRepository<Clinic, String> {
}
