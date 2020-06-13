package com.tiagods.prolink.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ObrigacaoNotFoundException extends RuntimeException {
    public ObrigacaoNotFoundException(String message){
        super(message);
    }
}
