package com.prolink.utils;

import com.prolink.exception.StructureNotFoundException;
import com.prolink.model.Pair;
import com.prolink.olders.model.Cliente;
import com.prolink.service.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class IOUtils {

    @Autowired
    private StructureService structureService;


    private Pair criar(Cliente cliente, Path destino){
        try {
            if (Files.notExists(destino)) Files.createDirectory(destino);
            return new Pair(cliente,destino);
        }catch (IOException e){
            //em caso de erro nenhum diretorio sera criado
            e.printStackTrace();
            return new Pair(cliente,null);
        }
    }
    private Pair mover(Cliente cliente, Path origem, Path destino){
        try{
            Files.move(origem, destino, StandardCopyOption.REPLACE_EXISTING);
            return new Pair(cliente,destino);
        }catch (IOException e){
            e.printStackTrace();
            return new Pair(cliente,origem);
        }
    }

    public Set<Path> listByDirectoryAndRegex(Path path, String regex) throws IOException{
        return Files.list(path)
                .filter(f->Files.isDirectory(f) && f.getFileName().toString()
                        .matches(regex))
                .collect(Collectors.toSet());
    }

    //verificar e criar estrutura de modelo
    public void verifyStructureInModel(Path estrutura) throws StructureNotFoundException {
        Path path = structureService.getModel().resolve(estrutura);
        createDirectories(path);
    }
    //verificar e criar estrutura de modelo
    public void createDirectories(Path path) throws StructureNotFoundException {
        try {
            if (Files.notExists(path)) Files.createDirectories(path);
        }catch (IOException e){
            throw new StructureNotFoundException("Falha ao criar a estrutura: "+e.getMessage(), e.getCause());
        }
    }
    //criar um diretorio
    public void createDirectory(Path path) throws StructureNotFoundException {
        try {
            if (Files.notExists(path)) Files.createDirectory(path);
        }catch (IOException e){
            throw new StructureNotFoundException("Falha ao criar a estrutura: "+e.getMessage(), e.getCause());
        }
    }
}
