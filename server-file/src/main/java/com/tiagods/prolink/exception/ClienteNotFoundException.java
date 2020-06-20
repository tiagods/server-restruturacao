package com.tiagods.prolink.exception;

public class ClienteNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;
    public ClienteNotFoundException(String mensagem) {
        super(mensagem);
    }
    public ClienteNotFoundException(String mensagem, Throwable causa) {
        super(mensagem,causa);
    }
}
