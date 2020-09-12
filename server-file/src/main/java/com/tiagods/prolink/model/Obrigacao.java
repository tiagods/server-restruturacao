package com.tiagods.prolink.model;

import com.tiagods.prolink.validation.PathLocation;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.Month;
import java.time.Year;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Obrigacao {
    @NotNull
    private Obrigacao.Tipo tipo;
    private Year ano;
    private Month mes;
    private Long cliente;
    @PathLocation
    private String dirForJob;

    @Getter
    public enum Tipo {
        PROLINKDIGITAL("PROLINK DIGITAL", "Geral/PROLINK DIGITAL", TipoArquivo.PASTA),
        IRPF("IRPF", "Obrigacao/IRPF", TipoArquivo.ARQUIVO),
        DIRF("DIRF", "Obrigacao/DIRF", TipoArquivo.ARQUIVO);
        private String descricao;
        private String estrutura;
        private TipoArquivo tipoArquivo;

        Tipo(String descricao, String estrutura, TipoArquivo tipoArquivo){
            this.descricao = descricao;
            this.estrutura = estrutura;
            this.tipoArquivo = tipoArquivo;
        }
    }

    @Override
    public String toString() {
        return "Obrigacao{" +
                "tipo=" + tipo +
                ", ano=" + ano +
                ", mes=" + mes +
                ", cliente=" + cliente +
                ", dirForJob='" + dirForJob + '\'' +
                '}';
    }
}
