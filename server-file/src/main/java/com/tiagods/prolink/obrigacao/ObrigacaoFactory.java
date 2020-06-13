package com.tiagods.prolink.obrigacao;

import com.tiagods.prolink.exception.StructureNotFoundException;
import com.tiagods.prolink.model.Obrigacao;

import java.time.Month;
import java.time.Year;
import java.util.Arrays;

public abstract class ObrigacaoFactory {
    public ObrigacaoContrato get(Obrigacao.Tipo tipo){
        if(tipo.equals(Obrigacao.Tipo.PROLINKDIGITAL)){
            return new ObrigacaoContratoImpl("PROLINK DIGITAL","Geral/PROLINK DIGITAL",
                    Arrays.asList(Year.class, Month.class));
        }
        throw new StructureNotFoundException("Recurso nao implementado");
    }
}
