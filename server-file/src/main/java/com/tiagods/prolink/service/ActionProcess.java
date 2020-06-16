package com.tiagods.prolink.service;

import com.tiagods.prolink.config.Regex;
import com.tiagods.prolink.exception.FolderCuncurrencyJob;
import com.tiagods.prolink.model.Cliente;
import com.tiagods.prolink.model.PathJob;
import com.tiagods.prolink.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ActionProcess {

    @Autowired
    private IOUtils ioUtils;

    @Autowired
    private Regex regex;

    @Autowired
    private ClienteIOService clientIOService;

    @Autowired
    private ArquivoService arquivoService;

    private Logger log = LoggerFactory.getLogger(getClass());

    //mover por pastas
    @Async
    public void moverPorPasta(PathJob pathJob, String nickName) throws FolderCuncurrencyJob {
        //Path path = Paths.get("c:/job");
        //Path novaEstrutura = Paths.get("GERAL","SAC");
        Path job = Paths.get(pathJob.getDirForJob());
        Path structure = Paths.get(pathJob.getEstrutura());

        clientIOService.verficarDiretoriosBaseECriar();

        //if(clientIOService.containsFolderToJob(job))
        //    throw new FolderCuncurrencyJob("Um processo ja esta em execução nessa pasta");
        //clientIOService.addFolderToJob(job);

        log.info("Iniciando movimentação de arquivos");
        try {
            clientIOService.verifyStructureInModel(structure);
            Optional<String> optionalS = Optional.ofNullable(nickName);
            String newRegex = "";
            if(optionalS.isPresent()) newRegex = regex.getInitByIdReplaceNickName().replace("nickName", nickName);
            else newRegex = regex.getInitById();

            Map<Path,String> mapClientes = ioUtils.listByDirectoryDefaultToMap(job, newRegex);
            log.info("Clientes encontrados com o regex: "+newRegex+" = "+mapClientes.size());

            Map<Path,Cliente> mapPath = new HashMap<>();
            mapClientes.keySet().forEach(c->{
                Long l = Long.parseLong(mapClientes.get(c));
                clientIOService.findMapClientById(l).ifPresent(r->mapPath.put(c,r));
            });
            //vai mover apenas os arquivos de dentro das pastas, as pastas irao continuar
            int i = 1;

            List<Path> list = new ArrayList<>(mapPath.keySet());
            for(Path p : list){
                if(clientIOService.containsFolderToJob(p)) continue;
                else {
                    Cliente cli = mapPath.get(p);
                    clientIOService.addFolderToJob(p);
                    log.info(structure.toString() + " - Processando item: " + i + " = cliente: " + cli.toString());
                    Path basePath = clientIOService.searchClientPathBaseAndCreateIfNotExists(cli);
                    if (basePath != null) {
                        try {
                            processarPorPasta(basePath, Files.list(p).iterator(), structure);
                        } catch (IOException e) {
                            log.error("Falha ao abrir pasta ".concat(p.toString()));
                        }
                    }
                    clientIOService.removeFolderToJob(p);
                    //ioUtils.deleteFolderIfEmptyRecursive(p);
                }
                i++;
            }
        }catch (IOException e){
            log.error(e.getMessage());
            log.info("Movimentação cancelada por erro");
        }
        log.info("Concluido movimentação de arquivos da pasta "+job.toString());
        //clientIOService.removeFolderToJob(job);

    }
    //inicia processo, vai percorrer todas as pastar e ira mover conteudo para um novo diretorio
    private void processarPorPasta(Path basePath, Iterator<Path> files, Path parent){
        while (files.hasNext()) {
            Path file  = files.next();
            if(Files.isDirectory(file)){
                try {
                    processarPorPasta(basePath, Files.list(file).iterator(), parent.resolve(file.getFileName()));
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
            else {
                //base - subpastas - arquivo
                Path estrutura = basePath.resolve(parent);
                ioUtils.mover(file, basePath, estrutura);
            }
        }
    }
}
