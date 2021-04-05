package com.tiagods.clientes.model;

public class ControleStatus {
    private String nome;
    private Long total = 0L;
    private Long totalFiltro = 0L;
    private String style = "-fx-fill : white; -fx-background-color: yellow";

    public ControleStatus(String nome, Long total) {
        this.nome = nome;
        this.total = total;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getTotalFiltro() {
        return totalFiltro;
    }

    public void setTotalFiltro(Long totalFiltro) {
        this.totalFiltro = totalFiltro;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
