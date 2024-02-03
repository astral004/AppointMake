package com.astral004.appointmentapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AppointmentDoesNotExistException extends RuntimeException{
    public AppointmentDoesNotExistException(String message){
        super(message);
    }
}
