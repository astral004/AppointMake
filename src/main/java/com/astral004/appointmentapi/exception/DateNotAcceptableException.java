package com.astral004.appointmentapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class DateNotAcceptableException extends RuntimeException {
    public DateNotAcceptableException(String s) {
        super(s);
    }
}
