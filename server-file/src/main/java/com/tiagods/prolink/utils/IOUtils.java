package com.tiagods.prolink.utils;

import com.tiagods.prolink.exception.StructureNotFoundException;
import com.tiagods.prolink.model.Pair;
import com.tiagods.prolink.model.Cliente;
import com.tiagods.prolink.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class IOUtils {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private FileService fileService;

    //criar diretorio para o cliente
    public Pair<Cliente, Path> create(Cliente client, Path destination){
        try {
            if (Files.notExists(destination)) Files.createDirectory(destination);
            return new Pair<>(client,destination);
        }catch (IOException e){
            //em caso de erro nenhum diretorio sera criado
            log.error(e.getMessage());
            return null;
        }
    }
    //verificar e criar estrutura de modelo
    public void createDirectories(Path path) throws IOException {
        if (Files.notExists(path)) Files.createDirectories(path);
    }
    //criar um diretorio
    public void createDirectory(Path path) throws IOException {
       if (Files.notExists(path)) Files.createDirectory(path);
    }
   //deletar de forma recursiva
    public void deleteFolderIfEmptyRecursive(Path path) throws IOException {
        if(Files.isDirectory(path)){

            try {
                deleteFolderIfEmpty(path);
                boolean exists = Files.exists(path);
                if (exists) {
                    Stream<Path> files = Files.list(path);
                    for (Path p : files.collect(Collectors.toSet())) {
                        deleteFolderIfEmptyRecursive(p);
                    }
                }
                deleteFolderIfEmpty(path);
            } catch(IOException e){
                log.error(e.getMessage());
            }
        }
    }

    private void deleteFolderIfEmpty(Path path) throws IOException{
        long q = Files.list(path).count();
        if(q == 0) {
//            Runtime.getRuntime().exec("cmd /c rmdir \"" + path.toString() + "\" /Q");
            FileSystemUtils.deleteRecursively(path);
        }
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
            fileService.convertAndSave(origin,destination);
            return new Pair<>(client,destination);
        }catch (IOException e){
            log.error(e.getMessage());
            return new Pair<>(client,origin);
        }
    }
    //mover arquivo com estrutura pre estabelecida
    public Path move(Path file, Path pathCli, Path structure){
        Path newStructureFile = structure.resolve(file.getFileName());
        Path finalFile = pathCli.resolve(newStructureFile);
        try {
            createDirectories(finalFile.getParent());
            Files.move(file, finalFile, StandardCopyOption.REPLACE_EXISTING);
            fileService.convertAndSave(file,finalFile);
            return finalFile;
        }catch (IOException e){
            fileService.saveError(file,finalFile,e.getMessage());
            log.error(e.getMessage());
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

    public boolean verifyIfExist(Path file){
        return Files.exists(file);
    }
}
