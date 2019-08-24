package com.prolink.model;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cliente implements Serializable {
    private int id;
    private String nome;
    private String status;
    private String cnpj;

    private String idFormatado;
    private String nomeFormatado;
    //cnpj sem os caracteres especiais
    private String cnpjFormatado;
    private boolean cnpjValido = false;

    public String getCnpj() {
        return cnpj;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
        String cnpjFormat = "(^\\d{2}\\d{3}\\d{3}\\d{4}\\d{2}$)";
        cnpj = cnpj.replace(".","").replace("/","").replace("-","");
        Matcher matcher = Pattern.compile(cnpjFormat).matcher(cnpj);
        if(matcher.find()) {
            this.cnpjFormatado = cnpj;
            cnpjValido = true;
        }
        else cnpjFormatado = "";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNomeFormatado(String nomeFormatado) {
        this.nomeFormatado = nomeFormatado;
    }

    @Override
    public String toString() {
        return this.idFormatado+"-"+this.nomeFormatado;
    }

    public String toFile(){
        return status+";"+nome+";"+cnpj;
    }
    public void getFile(String key,String value){
        setId(Integer.parseInt(key));
        String[] v = value.split(";");
        setStatus(v[0]);
        setNome(v[1]);
        setCnpj(this.cnpj=v.length>2?v[2]:"");
    }
    public String getIdFormatado() {
        return idFormatado;
    }
    public String getNomeFormatado() {
        return nomeFormatado;
    }

    public void setIdFormatado(String idFormatado) {
        this.idFormatado = idFormatado;
    }

    public String getCnpjFormatado() {
        return cnpjFormatado;
    }

    public boolean isCnpjValido() {
        return cnpjValido;
    }
}
