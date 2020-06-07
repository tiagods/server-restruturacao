package com.tiagods.prolink.controller;

import com.tiagods.prolink.model.Cliente;
import com.tiagods.prolink.validation.PathLocation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tipo {
    @NotNull
    private Obrigacao obrigacao;
    private Year ano;
    private Month mes;
    private int cliente;
    @PathLocation
    private String dirForJob;

    @Getter
    public enum Obrigacao {
        PROLINKDIGITAL("PROLINK DIGITAL",
                "Geral/PROLINK DIGITAL",
                Arrays.asList(Year.class,Month.class,Cliente.class),
                "{ANO}",
                "PROLINK DIGITAL {MES}{ANO}",
                "{APELIDO}");
        private String descricao;
        private String estrutura;
        private List config;
        private String pathAno;
        private String pathMes;
        private String apelido;

        Obrigacao(String descricao, String estrutura, List config, String pathAno, String pathMes, String apelido){
            this.descricao = descricao;
            this.estrutura = estrutura;
            this.config = config;
            this.pathAno = pathAno;
            this.pathMes = pathMes;
            this.apelido = apelido;
        }
    }
}
