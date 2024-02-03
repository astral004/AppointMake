package com.astral004.appointmentapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NullParametersException extends RuntimeException {
    public NullParametersException(String s) {
        super(s);
    }
}
