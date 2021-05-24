package com.tiagods.clientesender.model;

import lombok.Data;
import org.codehaus.groovy.util.StringUtil;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;

@Data
public class ClienteResource {
    Set<Path> arquivos;
    Cliente cliente;
    String template;
    ProcessoEnum processoEnum;
    String processo;
    EmailDto emailDto;
    boolean emailEnviado = false;
    String cid;
    long totalContatos = 0;
    NotificacaoControle controle;

    public static ClienteResource build(String cid, Cliente cliente, ProcessoEnum processoEnum){
        var resource = new ClienteResource();
        resource.processoEnum = processoEnum;
        resource.processo = processoEnum.name()+"2021";
        resource.cliente = cliente;
        resource.cid = cid;
        return resource;
    }

    public void setTotalContatos(long total, EmailDto email) {
        long para = Arrays.asList(email.getPara()).stream()
                .filter(c -> StringUtils.hasText(c)).count();
        long cc = Arrays.asList(email.getCc()).stream()
                .filter(c -> StringUtils.hasText(c)).count();
        long cco = Arrays.asList(email.getBcc()).stream()
                .filter(c -> StringUtils.hasText(c)).count();

        this.totalContatos = total + para + cc + cco;
    }
}

