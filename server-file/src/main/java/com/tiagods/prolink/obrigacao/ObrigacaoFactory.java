package com.tiagods.prolink.obrigacao;

import com.tiagods.prolink.model.Tipo;
import com.tiagods.prolink.exception.StructureNotFoundException;

public abstract class ObrigacaoFactory {
    public TemplateObrigacao get(Tipo.Obrigacao tipo){
        if(tipo.equals(Tipo.Obrigacao.PROLINKDIGITAL)){
            return new Template();
        }
        throw new StructureNotFoundException("Recurso nao implementado");
    }
}
