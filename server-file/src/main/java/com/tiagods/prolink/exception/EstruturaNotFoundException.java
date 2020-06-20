package com.tiagods.prolink.exception;

public class EstruturaNotFoundException extends RuntimeException {
    public EstruturaNotFoundException(String message){super(message);}
    public EstruturaNotFoundException(String message, Throwable throwable){super(message,throwable);}
}
