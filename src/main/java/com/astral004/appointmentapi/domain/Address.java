package com.astral004.appointmentapi.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@Document(collection = "addresses")
public class Address {

    String street;
    String city;
    String state;
    String zipCode;

    @Setter(AccessLevel.NONE)
    final static String country = "United States";
}
