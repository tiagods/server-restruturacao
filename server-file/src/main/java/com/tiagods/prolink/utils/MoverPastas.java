package com.tiagods.prolink.utils;

import com.tiagods.prolink.config.Regex;
import com.tiagods.prolink.exception.FolderCuncurrencyJob;
import com.tiagods.prolink.model.Cliente;
import com.tiagods.prolink.model.PathJob;
import com.tiagods.prolink.service.ArquivoService;
import com.tiagods.prolink.service.ClientIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class MoverPastas {

    @Autowired
    private IOUtils ioUtils;

    @Autowired
    private Regex regex;

    @Autowired
    private UtilsValidator validator;

    @Autowired
    private ClientIOService clientIOService;

    @Autowired
    private ArquivoService arquivoService;

    //mover por pastas
    @Async
    public void moveByFolder(PathJob pathJob) throws FolderCuncurrencyJob {
        //Path path = Paths.get("c:/job");
        //Path novaEstrutura = Paths.get("GERAL","SAC");

        clientIOService.verifyFoldersInBase();

        Path job = Paths.get(pathJob.getDirForJob());
        Path structure = Paths.get(pathJob.getStructure());

        if(clientIOService.containsFolderToJob(job))
            throw new FolderCuncurrencyJob("Um processo ja esta em execução nessa pasta");

        clientIOService.addFolderToJob(job);
        try {
            ioUtils.verifyStructureInModel(structure);
            Map<Path,String> mapClientes = ioUtils.listByDirectoryDefaultToMap(job, regex.getInitById());
            Map<Path,Cliente> mapPath = new HashMap<>();
            mapClientes.keySet().forEach(c->{
                Long l = Long.parseLong(mapClientes.get(c));
                clientIOService.findMapClientById(l).ifPresent(r->mapPath.put(c,r));
            });
            //vai mover apenas os arquivos de dentro das pastas, as pastas irao continuar
            for(Path p : mapPath.keySet()){
                Cliente cli = mapPath.get(p);
                process(cli, Files.list(p).iterator(),structure);
                ioUtils.deleteFolderIfEmptyRecursive(p);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        clientIOService.removeFolderToJob(job);

    }
    //inicia processo, vai percorrer todas as pastar e ira mover conteudo para um novo diretorio
    private void process(Cliente cli, Iterator<Path> files, Path parent) throws IOException{
        while (files.hasNext()) {
            Path file  = files.next();
            if(Files.isDirectory(file)){
                process(cli, Files.list(file).iterator(), parent.resolve(file.getFileName()));
            }
            else {
                Path basePath  = clientIOService.searchClientPathBaseAndCreateIfNotExists(cli);
                //base - subpastas - arquivo
                Path estrutura = basePath.resolve(parent);
                Path finalFile = ioUtils.move(file, basePath, estrutura);
                if(finalFile!=null){
                    arquivoService.convertAndSave(file,finalFile);
                }
            }
        }
    }
}
