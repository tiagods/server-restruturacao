package com.tiagods.obrigacoes.model;

import lombok.Getter;

@Getter
public class Pair<Cliente,Path> {
    private Cliente cliente;
    private Path path;

    private Pair(){}

    public Pair(Cliente cliente, Path path){
        this.cliente=cliente;
        this.path=path;
    }
}
