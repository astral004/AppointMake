package com.astral004.appointmentapi.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@ToString
@Document(collection = "doctors")
public class Doctor extends Person{
    @Id
    String id;
    String specialty;
}
