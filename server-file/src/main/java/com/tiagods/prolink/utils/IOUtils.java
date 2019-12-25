package com.tiagods.prolink.utils;

import com.tiagods.prolink.exception.StructureNotFoundException;
import com.tiagods.prolink.model.Pair;
import com.tiagods.prolink.model.Cliente;
import com.tiagods.prolink.service.ClientStructureService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class IOUtils {

    @Autowired
    private ClientStructureService structureService;

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
    //deletar de forma recursiva
    public void deleteFolderIfEmptyRecursive(Path path) throws IOException {
        if(Files.isDirectory(path)){
            try {
                File[] files = deleteFolderIfEmpty(path);
                if(files!=null){
                    for (File p : files) {
                        Path dir = p.toPath();
                        deleteFolderIfEmptyRecursive(dir);
                    }
                    //reanalizar
                    deleteFolderIfEmpty(path);
                }
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    private File[] deleteFolderIfEmpty(Path path) throws IOException{
        File[] files = path.toFile().listFiles();
        if(files.length == 0) {
            FileUtils.deleteDirectory(path.toFile());
            return null;
        }
        else return files;
    }
    //listar diretorios e por regex
    public Set<Path> listByDirectoryAndRegex(Path path, String regex) throws IOException{
        return Files.list(path)
                .filter(f->Files.isDirectory(f) && f.getFileName().toString().matches(regex))
                .collect(Collectors.toSet());
    }
    //listar diretorios e trazer o map<diretorio, clienteApelido>
    public Map<Path,String> listByDirectoryDefaultToMap(Path path, String regex) throws IOException{
        Set<Path> paths = listByDirectoryAndRegex(path, regex);
        Map<Path,String> parentMap = new HashMap<>();
        paths.forEach(c-> parentMap.put(c,c.getFileName().toString().substring(0,4)));
        return parentMap;
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
    //mover arquivo com estrutura pre estabelecida
    public Path move(Path file, Path pathCli, Path structure){
        Path newStructureFile = structure.resolve(file.getFileName());
        Path finalFile = pathCli.resolve(newStructureFile);
        createDirectories(finalFile.getParent());
        try {
            Files.move(file, finalFile, StandardCopyOption.REPLACE_EXISTING);
            return finalFile;
        }catch (IOException e){
            return null;
        }
    }
    //buscar por ID nos 4 primeiros caracteres
    public Optional<Path> searchFolderById(Cliente client, Set<Path> paths){
        return paths
                .stream()
                .filter(n->n.getFileName().toString().substring(0,4).equals(client.getIdFormatado()))
                .findFirst();
    }

    //verificar e criar estrutura de modelo
    public void verifyStructureInModel(Path structure) throws StructureNotFoundException {
        Path path = structureService.getModel().resolve(structure);
        createDirectories(path);
    }

    public boolean verifyIfExist(Path file){
        return Files.exists(file);
    }

}
