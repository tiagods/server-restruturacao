package com.tiagods.prolink.obrigacao;

import java.util.List;
import java.util.Map;

public class ObrigacaoContratoImpl implements ObrigacaoContrato {
    
    private String descricao;
    private String estrutura;
    private Map<Class,OrdemString> periodos;

    private String pathAno;
    private String pathMes;

    public enum OrdemString {
        INICIO, MEIO, FIM
    }

    public ObrigacaoContratoImpl(String descricao, String estrutura, Map<Class,OrdemString> periodos) {
        this.descricao = descricao;
        this.estrutura = estrutura;
        this.periodos = periodos;
    }

    public boolean contains(Class object){
        return periodos.containsKey(object);
    }

    public void getMes(String nomePasta){

    }

}
