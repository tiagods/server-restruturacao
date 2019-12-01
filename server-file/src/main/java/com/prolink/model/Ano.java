package com.prolink.model;

import java.io.Serializable;
import java.util.Objects;

public class Ano implements Serializable {
    private String nome;

    public Ano(String nome){
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
        Ano ano = (Ano) o;
        return Objects.equals(nome, ano.nome);
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
