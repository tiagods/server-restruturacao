package com.prolink.model;

import java.io.Serializable;
import java.util.Objects;

public class Raiz implements Serializable{
    private String nome;

    public Raiz(String nome){
        this.nome=nome;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Raiz raiz = (Raiz) o;
        return Objects.equals(nome, raiz.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }

    @Override
    public String toString() {
        return this.nome;
    }
}
