package com.prolink.service;

import com.prolink.model.Pair;
import com.prolink.model.Cliente;
import com.prolink.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.*;

@Service
public class ClientIOService {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private IOUtils ioUtils;

    @Autowired
    private StructureService structureService;

    private Map<Cliente, Path> cliMap = new HashMap<>();

    Set<Cliente> clientSet = new HashSet<>();

    static{
        //Cliente cli = new Cliente(1,)
    }
    @PostConstruct
    public void onInit(){
        log.info("Iniciando mapeamento de clientes");
        mapClient(structureService.listAllInBaseAndShutdown());
        log.info("Concluindo mapeamento");
    }

    //mapeamento de pastas, cuidado ao usar em produção
    public void mapClient(Set<Path> files){
        clientSet.forEach(c->{
            Optional<Path> file = ioUtils.searchFolderById(c,files);

            Pair<Cliente,Path> pair = null;

            Path desligados = structureService.getShutdown();
            Path base = structureService.getBase();

            Path destinoDesligada = structureService.getShutdown().resolve(c.toString());
            Path destinoAtiva = structureService.getBase().resolve(c.toString());

            if(file.isPresent()) {
                //verificar nome do arquivo se esta de acordo com a norma
                boolean nomeCorreto = file.get().getFileName().toString().equals(c.toString());
                //caminho do diretorio
                if (c.getStatus().equalsIgnoreCase("Desligada")) {
                    boolean localCorreto = file.get().getParent().equals(desligados);
                    if(!localCorreto || !nomeCorreto) pair = ioUtils.move(c, file.get(), destinoDesligada);
                }
                else{
                    boolean localCorreto = file.get().getParent().equals(base);
                    if(!localCorreto || !nomeCorreto) pair = ioUtils.move(c, file.get(), destinoAtiva);
                }
            }
            else{
                //criar pasta oficial caso não exista
                if(c.getStatus().equalsIgnoreCase("Desligada")) pair = ioUtils.create(c, destinoDesligada);
                else pair = ioUtils.create(c,destinoAtiva);
            }
            cliMap.put(pair.getCliente(),pair.getPath());
        });
    }
    public Path searchClient(Cliente c) {
        return cliMap.get(c);
    }
}
