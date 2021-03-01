package com.tiagods.obrigacoes.exception;

public class InvalidNickException extends Exception{
        private static final long serialVersionUID = 1L;
        public InvalidNickException(String mensagem) {
            super(mensagem);
        }
        public InvalidNickException(String mensagem, Throwable causa) {
            super(mensagem,causa);
        }
}
