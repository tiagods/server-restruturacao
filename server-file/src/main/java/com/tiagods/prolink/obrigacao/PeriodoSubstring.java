package com.tiagods.prolink.obrigacao;

import lombok.*;
import org.springframework.data.util.Pair;

@Getter
@Setter
@AllArgsConstructor
public class PeriodoSubstring {
    //usado para capturar uma expressão de uma string ex: 04 de 01-04/2011, first=01-, second=/2011
    private Pair<String, String> ano;
    private Pair<String, String> mes;

    public Pair<String, String> getPair(Periodo periodo) {
        if(periodo.equals(Periodo.ANO)){
            return ano;
        } else return mes;
    }

    @Override
    public String toString() {
        return "{" +
                "ano=" + ano +
                ", mes=" + mes +
                '}';
    }
}
