package com.astral004.appointmentapi.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@Document(collection = "patients")
public class Patient extends Person{
    @Id
    String id;
    String insuranceId;
    String memberId;
    Boolean firstTime;
}
