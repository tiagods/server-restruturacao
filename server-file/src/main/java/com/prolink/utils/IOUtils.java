package com.prolink.utils;

import com.prolink.exception.StructureNotFoundException;
import com.prolink.model.Pair;
import com.prolink.model.Cliente;
import com.prolink.service.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class IOUtils {

    @Autowired
    private StructureService structureService;

    //criar diretorio para o cliente
    public Pair<Cliente, Path> create(Cliente client, Path destination){
        try {
            if (Files.notExists(destination)) Files.createDirectory(destination);
            return new Pair<>(client,destination);
        }catch (IOException e){
            //em caso de erro nenhum diretorio sera criado
            e.printStackTrace();
            return new Pair<Cliente, Path>(client,null);
        }
    }
    //tentar mover, se nao conseguir usar o diretorio de origem
    public Pair<Cliente, Path> move(Cliente client, Path origin, Path destination){
        try{
            Files.move(origin, destination, StandardCopyOption.REPLACE_EXISTING);
            return new Pair<>(client,destination);
        }catch (IOException e){
            e.printStackTrace();
            return new Pair<>(client,origin);
        }
    }
    //buscar por ID nos 4 primeiros caracteres
    public Optional<Path> searchFolderById(Cliente client, Set<Path> paths){
        return paths
                .stream()
                .filter(n->n.getFileName().toString().substring(0,4).equals(client.getIdFormatado()))
                .findFirst();
    }
    //listar diretorios e por regex
    public Set<Path> listByDirectoryAndRegex(Path path, String regex) throws IOException{
        return Files.list(path)
                .filter(f->Files.isDirectory(f) && f.getFileName().toString().matches(regex))
                .collect(Collectors.toSet());
    }

    //verificar e criar estrutura de modelo
    public void verifyStructureInModel(Path structure) throws StructureNotFoundException {
        Path path = structureService.getModel().resolve(structure);
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
