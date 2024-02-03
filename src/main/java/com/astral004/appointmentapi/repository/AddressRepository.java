package com.astral004.appointmentapi.repository;

import com.astral004.appointmentapi.domain.Address;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AddressRepository extends MongoRepository<Address, String> {
}
