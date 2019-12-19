package com.tiagods.prolink.model;

import java.io.Serializable;
import java.util.Objects;

public class Obrigacao implements Serializable{
        private String nome;

        public Obrigacao(String nome) {
                this.nome = nome;
        }

        public String getNome() {
                return nome;
        }

        public void setNome(String nome) {
                this.nome = nome;
        }
        @Override
        public String toString() {
                return this.nome;
        }
        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Obrigacao obrigacao = (Obrigacao) o;
                return Objects.equals(nome, obrigacao.nome);
        }

        @Override
        public int hashCode() {
                return Objects.hash(nome);
        }
}
