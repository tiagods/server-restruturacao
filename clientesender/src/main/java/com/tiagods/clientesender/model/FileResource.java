package com.tiagods.clientesender.model;

import lombok.Data;
import org.springframework.util.StringUtils;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Data
public class FileResource {
    List<Path> arquivos;
    Path dir;
    String cid;
    String regex;
    ProcessoEnum processoEnum;
    List<Cliente> clientes;

    public static FileResource build(String cid, Path dir, ProcessoEnum processoEnum, String regex){
        FileResource resource = new FileResource();
        resource.dir = dir;
        resource.processoEnum = processoEnum;
        resource.regex = regex;
        resource.cid = StringUtils.hasText(cid) ? cid : UUID.randomUUID().toString();
        return resource;
    }
}

