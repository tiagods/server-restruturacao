package com.tiagods.prolink.model;

public class Pair<Cliente,Path> {
    private Cliente cliente;
    private Path path;
    public Pair(Cliente cliente, Path path){
        this.cliente=cliente;
        this.path=path;
    }
    public Cliente getCliente() {
        return cliente;
    }
    public Path getPath() {
        return path;
    }
}
