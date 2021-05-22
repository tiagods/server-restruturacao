package com.tiagods.clientesender.model;

import lombok.Data;

import java.nio.file.Path;
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
    int totalContatos = 0;
    NotificacaoControle controle;

    public static ClienteResource build(String cid, Cliente cliente, ProcessoEnum processoEnum){
        var resource = new ClienteResource();
        resource.processoEnum = processoEnum;
        resource.processo = processoEnum.name()+"2021";
        resource.cliente = cliente;
        resource.cid = cid;
        return resource;
    }
}

