package com.tiagods.prolink.utils;

import com.tiagods.prolink.config.Regex;
import com.tiagods.prolink.model.Cliente;
import com.tiagods.prolink.service.ClientIOService;
import com.tiagods.prolink.service.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
public class MoverPorPastas {

    @Autowired
    private StructureService structureService;

    @Autowired
    private IOUtils ioUtils;

    @Autowired
    private Regex regex;

    @Autowired
    private UtilsValidator validator;

    @Autowired
    private ClientIOService clientIOService;

    private Path novaEstrutura;

    private void iniciar(){
        Path path = Paths.get("\\\\PLKSERVER\\Todos Departamentos\\SAC");
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
                moverRecursive(arquivo, basePath, estrutura);
           }
        }
    }
    private void moverRecursive(Path arquivo, Path pathCli, Path estrutura) throws IOException{
        Path novoArquivo = estrutura.resolve(arquivo.getFileName());
        Path arquivoFinal = pathCli.resolve(novoArquivo);
        if(Files.notExists(arquivoFinal.getParent())) Files.createDirectories(arquivoFinal.getParent());
        Files.move(arquivo, arquivoFinal, StandardCopyOption.REPLACE_EXISTING);
        System.out.println(arquivo + "\t>\t" + arquivoFinal);
        salvarRelatorio(arquivo.toString(),arquivoFinal.toString());
    }

    void salvarRelatorio(String de, String to) throws IOException {
        Path path = Paths.get("result.csv");
        if (Files.notExists(path)) Files.createFile(path);
        FileWriter fw = new FileWriter(path.toFile(), true);
        fw.write(de + "\t" + to);
        fw.write(System.getProperty("line.separator"));
        fw.flush();
        fw.close();
    }

}
