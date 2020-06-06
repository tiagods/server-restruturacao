package com.tiagods.prolink.utils;

import com.tiagods.prolink.model.Pair;
import com.tiagods.prolink.model.Cliente;
import com.tiagods.prolink.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Slf4j
public class IOUtils {

    @Autowired
    private FileService fileService;

    //criar diretorio para o cliente
    public Pair<Cliente, Path> criarDiretorioCliente(Cliente client, Path destino){
        try {
            if (Files.notExists(destino)) Files.createDirectory(destino);
            return new Pair<>(client,destino);
        }catch (IOException e){
            //em caso de erro nenhum diretorio sera criado
            log.error(e.getMessage());
            return null;
        }
    }
    //verificar e criar estrutura de modelo
    public void criarDiretorios(Path path) throws IOException {
        if (Files.notExists(path)) Files.createDirectories(path);
    }
    //criar um diretorio
    public void criarDiretorio(Path path) throws IOException {
       if (Files.notExists(path)) Files.createDirectory(path);
    }
   //deletar de forma recursiva
    public void deletarPastaSeVazioRecursivo(Path path) throws IOException {
        if(Files.isDirectory(path)){

            try {
                deletarPastaSeVazio(path);
                boolean exists = Files.exists(path);
                if (exists) {
                    Stream<Path> files = Files.list(path);
                    for (Path p : files.collect(Collectors.toSet())) {
                        deletarPastaSeVazioRecursivo(p);
                    }
                }
                deletarPastaSeVazio(path);
            } catch(IOException e){
                log.error(e.getMessage());
            }
        }
    }

    private void deletarPastaSeVazio(Path path) throws IOException{
        long q = Files.list(path).count();
        if(q == 0) {
//            Runtime.getRuntime().exec("cmd /c rmdir \"" + path.toString() + "\" /Q");
            FileSystemUtils.deleteRecursively(path);
        }
    }
    //listar diretorios e por regex
    public Set<Path> filtrarPorDiretorioERegex(Path path, String regex) throws IOException{
        return Files.list(path)
                .filter(f->Files.isDirectory(f) && f.getFileName().toString().matches(regex))
                .collect(Collectors.toSet());
    }
    //listar diretorios e trazer o map<diretorio, clienteApelido>
    public Map<Path,String> listByDirectoryDefaultToMap(Path path, String regex) throws IOException{
        Set<Path> paths = filtrarPorDiretorioERegex(path, regex);
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
            criarDiretorios(finalFile.getParent());
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
