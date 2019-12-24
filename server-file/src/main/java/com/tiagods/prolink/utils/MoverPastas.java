package com.tiagods.prolink.utils;

import com.tiagods.prolink.config.Regex;
import com.tiagods.prolink.dto.ArquivoDTO;
import com.tiagods.prolink.model.Cliente;
import com.tiagods.prolink.service.ArquivoService;
import com.tiagods.prolink.service.ClientIOService;
import com.tiagods.prolink.service.ClientStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
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

    private Path novaEstrutura;

    public void iniciar(){
        Path path = Paths.get("/home/tiago/job");
        try {
            novaEstrutura = Paths.get("GERAL","SAC");
            ioUtils.verifyStructureInModel(novaEstrutura);
            Map<Path,String> mapClientes = ioUtils.listByDirectoryDefaultToMap(path, regex.getInitById());
            Map<Cliente,Path> mapPath = new HashMap<>();
            mapClientes.keySet().forEach(c->{
                Long l = Long.parseLong(mapClientes.get(c));
                clientIOService.searchClientById(l).ifPresent(r->mapPath.put(r,c));
            });
            //vai mover apenas os arquivos de dentro das pastas, as pastas irao continuar
            for(Cliente cli : mapPath.keySet()){
                Path p = mapPath.get(cli);
                processar(cli, Files.list(p).iterator(),novaEstrutura);
            };
            ioUtils.deleteFolderIfEmptyRecursive(path);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Autowired
    ArquivoService arquivoService;

    private void processar(Cliente cli, Iterator<Path> files, Path parent) throws IOException{
        while (files.hasNext()) {
            Path arquivo  = files.next();
            if(Files.isDirectory(arquivo)){
                processar(cli, Files.list(arquivo).iterator(), parent.resolve(arquivo.getFileName()));
            }
            else {
                Path basePath  = clientIOService.searchClientPathBase(cli);
                //base - subpastas - arquivo
                Path estrutura = basePath.resolve(parent);
                Path finalFile = ioUtils.move(arquivo, basePath, estrutura);
                if(finalFile!=null){
                    ArquivoDTO arquivoDTO = new ArquivoDTO();
                    arquivoDTO.setData(new Date());
                    arquivoDTO.setDestino(finalFile.toString());
                    arquivoDTO.setNovoNome(finalFile.getFileName().toString());
                    arquivoDTO.setOrigem(arquivo.toString());
                    arquivoDTO.setNome(arquivo.getFileName().toString());
                    arquivoService.salvar(arquivoDTO);
                }
            }
        }
    }



}
