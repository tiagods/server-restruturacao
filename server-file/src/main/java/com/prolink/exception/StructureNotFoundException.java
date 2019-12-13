package com.prolink.exception;

import java.io.IOException;

public class StructureNotFoundException extends RuntimeException {
    public StructureNotFoundException(String message){super(message);}
    public StructureNotFoundException(String message, Throwable throwable){super(message,throwable);}
}
