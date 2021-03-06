package com.tiagods.obrigacoes.model;

import com.tiagods.obrigacoes.validation.PathLocation;
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
        PROLINKDIGITAL("PROLINK DIGITAL_FISCAL", "Geral/PROLINK DIGITAL", TipoArquivo.PASTA),
        IRPF("IRPF", "Obrigacao/IRPF", TipoArquivo.ARQUIVO),
        DIRF("DIRF", "Obrigacao/DIRF", TipoArquivo.ARQUIVO),
        DCTF("DCTF", "Obrigacao/DCTF", TipoArquivo.ARQUIVO),
        GIAICMS("GIA-ICMS", "Obrigacao/GIA-ICMS", TipoArquivo.ARQUIVO),
        REINF("REINF","Obrigacao/REINF", TipoArquivo.ARQUIVO),
        SEDIFDESTDA("SEDIF-DESTDA", "Obrigacao/SEDIF-DESTDA", TipoArquivo.ARQUIVO),
        SIMPLESNACIONALPGDASD("SIMPLES NACIONAL-PGDASD", "Obrigacao/SIMPLES NACIONAL-PGDASD", TipoArquivo.ARQUIVO),
        SINTEGRA("SINTEGRA", "Obrigacao/SINTEGRA", TipoArquivo.ARQUIVO),
        SPEDICMSIPI("SPED ICMS IPI", "Obrigacao/SPED ICMS IPI", TipoArquivo.ARQUIVO),
        SPEDPISCOFINS("SPED PIS COFINS", "Obrigacao/SPED PIS COFINS", TipoArquivo.ARQUIVO);

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
