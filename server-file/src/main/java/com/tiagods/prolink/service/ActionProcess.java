package com.tiagods.prolink.service;

import com.tiagods.prolink.config.Regex;
import com.tiagods.prolink.exception.FolderCuncurrencyJob;
import com.tiagods.prolink.model.Cliente;
import com.tiagods.prolink.model.PathJob;
import com.tiagods.prolink.utils.IOUtils;
import com.tiagods.prolink.utils.UtilsValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class ActionProcess {

    @Autowired
    private IOUtils ioUtils;

    @Autowired
    private Regex regex;

    @Autowired
    private UtilsValidator validator;

    @Autowired
    private ClientIOService clientIOService;

    @Autowired
    private FileService fileService;

    private Logger log = LoggerFactory.getLogger(getClass());

    //mover por pastas
    @Async
    public void moveByFolder(PathJob pathJob) throws FolderCuncurrencyJob {
        //Path path = Paths.get("c:/job");
        //Path novaEstrutura = Paths.get("GERAL","SAC");
        Path job = Paths.get(pathJob.getDirForJob());
        Path structure = Paths.get(pathJob.getStructure());

        clientIOService.verifyFoldersInBase();

        if(clientIOService.containsFolderToJob(job))
            throw new FolderCuncurrencyJob("Um processo ja esta em execução nessa pasta");

        clientIOService.addFolderToJob(job);
        log.info("Iniciando movimentação de arquivos");
        try {
            clientIOService.verifyStructureInModel(structure);
            Map<Path,String> mapClientes = ioUtils.listByDirectoryDefaultToMap(job, regex.getInitById());
            Map<Path,Cliente> mapPath = new HashMap<>();
            mapClientes.keySet().forEach(c->{
                Long l = Long.parseLong(mapClientes.get(c));
                clientIOService.findMapClientById(l).ifPresent(r->mapPath.put(c,r));
            });
            //vai mover apenas os arquivos de dentro das pastas, as pastas irao continuar
            for(Path p : mapPath.keySet()){
                Cliente cli = mapPath.get(p);
                Path basePath  = clientIOService.searchClientPathBaseAndCreateIfNotExists(cli);
                if(basePath != null)
                    processByFolder(basePath, Files.list(p).iterator(),structure);
                //ioUtils.deleteFolderIfEmptyRecursive(p);
            }
            log.info("Concluido movimentação de arquivos");
        }catch (IOException e){
            log.error(e.getMessage());
            log.info("Movimentação cancelada por erro");
        }
        clientIOService.removeFolderToJob(job);

    }
    //inicia processo, vai percorrer todas as pastar e ira mover conteudo para um novo diretorio
    private void processByFolder(Path basePath, Iterator<Path> files, Path parent){
        while (files.hasNext()) {
            Path file  = files.next();
            if(Files.isDirectory(file)){
                try {
                    processByFolder(basePath, Files.list(file).iterator(), parent.resolve(file.getFileName()));
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
            else {
                //base - subpastas - arquivo
                Path estrutura = basePath.resolve(parent);
                ioUtils.move(file, basePath, estrutura);
            }
        }
    }
}
