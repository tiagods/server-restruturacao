package com.tiagods.prolink.obrigacao;

import java.util.List;

public class Template implements TemplateObrigacao {
    
    private String descricao;
    private String estrutura;
    private List<Class> config;
    private String pathAno;
    private String pathMes;
    
    public Template(String descricao, String es,List<Class> periodo) {

    }

    public boolean contains(Class object){
        return config.contains(object);
    }

}
