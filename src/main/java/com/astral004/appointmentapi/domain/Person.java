package com.astral004.appointmentapi.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@ToString
abstract public class Person {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String addressId;
    private String socialSecurityNumber;
    private LocalDate birthday;
}
