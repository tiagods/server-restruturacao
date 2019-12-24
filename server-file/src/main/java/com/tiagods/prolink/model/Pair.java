package com.tiagods.prolink.model;

import lombok.Getter;

@Getter
public class Pair<Cliente,Path> {
    private Cliente cliente;
    private Path path;

    public Pair(Cliente cliente, Path path){
        this.cliente=cliente;
        this.path=path;
    }
}
