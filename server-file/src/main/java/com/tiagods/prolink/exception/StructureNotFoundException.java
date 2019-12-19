package com.tiagods.prolink.exception;

public class StructureNotFoundException extends RuntimeException {
    public StructureNotFoundException(String message){super(message);}
    public StructureNotFoundException(String message, Throwable throwable){super(message,throwable);}
}
