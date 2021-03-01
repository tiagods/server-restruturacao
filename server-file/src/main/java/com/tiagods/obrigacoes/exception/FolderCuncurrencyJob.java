package com.tiagods.obrigacoes.exception;

public class FolderCuncurrencyJob extends Exception{
    private static final long serialVersionUID = 1L;
    public FolderCuncurrencyJob(String mensagem) {
        super(mensagem);
    }
    public FolderCuncurrencyJob(String mensagem, Throwable causa) {
        super(mensagem,causa);
    }
}
