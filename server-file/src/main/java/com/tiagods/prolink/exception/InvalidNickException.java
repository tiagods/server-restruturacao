package com.tiagods.prolink.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InvalidNickException extends Exception{
        private static final long serialVersionUID = 1L;
        public InvalidNickException(String mensagem) {
            super(mensagem);
        }
        public InvalidNickException(String mensagem, Throwable causa) {
            super(mensagem,causa);
        }
}
