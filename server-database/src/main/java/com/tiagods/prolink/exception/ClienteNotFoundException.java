package com.tiagods.prolink.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ClienteNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;
    public ClienteNotFoundException(String mensagem) {
        super(mensagem);
    }
    public ClienteNotFoundException(String mensagem, Throwable causa) {
        super(mensagem,causa);
    }
}
