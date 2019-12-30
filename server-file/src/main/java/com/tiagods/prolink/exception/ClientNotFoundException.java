package com.tiagods.prolink.exception;

public class ClientNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;
    public ClientNotFoundException(String mensagem) {
        super(mensagem);
    }
    public ClientNotFoundException(String mensagem, Throwable causa) {
        super(mensagem,causa);
    }
}
