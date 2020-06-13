package com.tiagods.prolink.model;

import com.tiagods.prolink.utils.MyStringUtils;
import com.tiagods.prolink.utils.UtilsValidator;

import java.io.Serializable;
import java.util.Objects;

public class Cliente implements Serializable {
    private Long id;
    private String nome;
    private String status;
    //private String cnpj;
    private String idFormatado;
    private String nomeFormatado;
    //cnpj sem os caracteres especiais
    private String cnpjFormatado;
    private boolean cnpjValido = false;

//    public String getCnpj() { return cnpj; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public Cliente(Long id, String nome, String status, String cnpj){
        this.id = id;
        this.idFormatado = MyStringUtils.novoApelido(id);
        this.nome = MyStringUtils.normalizer(nome).toUpperCase();
        this.nomeFormatado = MyStringUtils.encurtarNome(this.nome);
        this.status = status;
        String newCnpj = MyStringUtils.cnpjNumerico(cnpj);
        this.cnpjFormatado = "";
        this.cnpjValido = false;
        if(UtilsValidator.validarCnpjNumerico(newCnpj)){
            cnpjValido = true;
            cnpjFormatado = newCnpj;
        }
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    //public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return this.idFormatado+"-"+this.nomeFormatado;
    }

    public String getIdFormatado() {
        return idFormatado;
    }

    public String getNomeFormatado() {
        return nomeFormatado;
    }

    public String getCnpjFormatado() {
        return cnpjFormatado;
    }

    public boolean isCnpjValido() {
        return cnpjValido;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return id == cliente.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
