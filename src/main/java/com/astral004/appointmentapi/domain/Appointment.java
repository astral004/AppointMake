package com.astral004.appointmentapi.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter

@Document(collection = "appointments")
public class Appointment {
    @Id
    String id;
    String patientId;
    String doctorId;
    String clinicId;
    LocalDate timing;
    String description;
    String insuranceId;
    Integer copay;
    String status;
}
