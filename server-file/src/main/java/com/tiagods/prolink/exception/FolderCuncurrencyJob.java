package com.tiagods.prolink.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FolderCuncurrencyJob extends Exception{
    private static final long serialVersionUID = 1L;
    public FolderCuncurrencyJob(String mensagem) {
        super(mensagem);
    }
    public FolderCuncurrencyJob(String mensagem, Throwable causa) {
        super(mensagem,causa);
    }
}
