package com.tiagods.prolink.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class StructureNotFoundException extends RuntimeException {
    public StructureNotFoundException(String message){super(message);}
    public StructureNotFoundException(String message, Throwable throwable){super(message,throwable);}
}
