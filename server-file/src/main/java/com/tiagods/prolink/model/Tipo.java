package com.tiagods.prolink.model;

import com.tiagods.prolink.validation.PathLocation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.Month;
import java.time.Year;
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
                "Geral/PROLINK DIGITAL");
        private String descricao;
        private String estrutura;
        private List config;

        Obrigacao(String descricao, String estrutura){
            this.descricao = descricao;
            this.estrutura = estrutura;
        }
    }
    
    
    
}
