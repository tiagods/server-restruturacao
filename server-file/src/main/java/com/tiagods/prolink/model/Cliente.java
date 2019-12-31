package com.tiagods.prolink.model;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cliente implements Serializable {
    private Long id;
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
        this.idFormatado = newId(id);
        this.nome = normalize(nome).toUpperCase();
        this.nomeFormatado = newName(nome);
        this.status = status;
        this.cnpj=cnpj;
        String cnpjFormat = "(^\\d{2}\\d{3}\\d{3}\\d{4}\\d{2}$)";
        cnpj = cnpj.replace(".","").replace("/","").replace("-","");
        Matcher matcher = Pattern.compile(cnpjFormat).matcher(cnpj);
        if(matcher.find()) {
            this.cnpjFormatado = cnpj;
            cnpjValido = true;
        }
        else cnpjFormatado = "";

    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

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

    private String normalize(String nome){
        String novo= Normalizer.normalize(nome, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
        return novo;
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

    private String newId(Long id){
        int size = String.valueOf(id).length();
        return size==1?"000"+id:
                (size==2?"00"+id:
                        (size==3?"0"+id:String.valueOf(id))
                );
    }
    private String newName(String nome) {
        String novoNome = removeSpecialsCharacters(nome);
        String[] valor = novoNome.split(" ");
        StringBuilder v1 = new StringBuilder();
        int limite = 20;
        for(int i=0; i<valor.length;i++){
            int size = v1.length();
            if(size+valor[i].length()>=limite) break;
            if(contains(valor[i]) && valor[i+1]!=null){
                if(!contains(valor[i+1]))
                    v1.append(
                            (size+valor[i].length()+valor[i+1].length()<limite)
                            ? valor[i]+" "+valor[i+1]+" ": "");
            }
            else v1.append(
                    (size+valor[i].length()<limite)
                    ? valor[i]+" ": "");
        }
        return v1.toString().trim();
    }

    private String removeSpecialsCharacters(String valor){
        String newValor = valor;
        //String chars = "\"!@#$%¨&*()_+{}|<>:?'-=[]\\,.;/£¢¬§ªº°";
        String chars = "\"!@#$%¨*()_+{}|<>:?'-=[]\\,.;/£¢¬§ªº°";
        for(char b : chars.toCharArray())
            newValor = newValor.replace(String.valueOf(b), " ");
        newValor = newValor.replace("   "," ");
        newValor = newValor.replace("  "," ");
        return newValor;
    }
    private boolean contains(String value){
        String[] array = new String[]{"DE","EM","A","E"};
        for(String s : array){
            return value.trim().equals(s);
        }
        return false;
    }
}
