package com.tiagods.prolink.utils;

import com.tiagods.prolink.config.Regex;
import com.tiagods.prolink.model.Cliente;
import com.tiagods.prolink.service.ArquivoService;
import com.tiagods.prolink.service.ClientIOService;
import com.tiagods.prolink.service.ClientStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private ClientStructureService structureService;

    @Autowired
    private IOUtils ioUtils;

    @Autowired
    private Regex regex;

    @Autowired
    private UtilsValidator validator;

    @Autowired
    private ClientIOService clientIOService;

    @Autowired
    ArquivoService arquivoService;

    //mover por pastas
    public void moveByFolder(Path job, Path structure){
        //Path path = Paths.get("c:/job");
        //Path novaEstrutura = Paths.get("GERAL","SAC");
        try {
            ioUtils.verifyStructureInModel(structure);
            Map<Path,String> mapClientes = ioUtils.listByDirectoryDefaultToMap(job, regex.getInitById());
            Map<Cliente,Path> mapPath = new HashMap<>();
            mapClientes.keySet().forEach(c->{
                Long l = Long.parseLong(mapClientes.get(c));
                clientIOService.searchClientById(l).ifPresent(r->mapPath.put(r,c));
            });
            //vai mover apenas os arquivos de dentro das pastas, as pastas irao continuar
            for(Cliente cli : mapPath.keySet()){
                Path p = mapPath.get(cli);
                process(cli, Files.list(p).iterator(),structure);
            };
            ioUtils.deleteFolderIfEmptyRecursive(job);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    //inicia processo, vai percorrer todas as pastar e ira mover conteudo para um novo diretorio
    private void process(Cliente cli, Iterator<Path> files, Path parent) throws IOException{
        while (files.hasNext()) {
            Path file  = files.next();
            if(Files.isDirectory(file)){
                process(cli, Files.list(file).iterator(), parent.resolve(file.getFileName()));
            }
            else {
                Path basePath  = clientIOService.searchClientPathBase(cli);
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
