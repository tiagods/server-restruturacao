package com.tiagods.prolink.utils;

import com.tiagods.prolink.model.Pair;
import com.tiagods.prolink.model.Cliente;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class IOUtils {

    //criar diretorio para o cliente
    public static Pair<Cliente, Path> criarDiretorioCliente(Cliente client, Path destino){
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
    public static void criarDiretorios(Path path) throws IOException {
        if (Files.notExists(path)) Files.createDirectories(path);
    }

    //criar um diretorio
    public static void criarDiretorio(Path path) throws IOException {
       if (Files.notExists(path)) Files.createDirectory(path);
    }

   //deletar de forma recursiva
    public static void deletarPastaSeVazioRecursivo(Path path) throws IOException {
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

    private static void deletarPastaSeVazio(Path path) throws IOException{
        long q = Files.list(path).count();
        if(q == 0) {
//            Runtime.getRuntime().exec("cmd /c rmdir \"" + path.toString() + "\" /Q");
            FileSystemUtils.deleteRecursively(path);
        }
    }

    //listar diretorios e por regex
    public static Set<Path> filtrarPorDiretorioERegex(Path path, String regex) throws IOException{
        return Files.list(path)
                .filter(f->Files.isDirectory(f) && f.getFileName().toString().matches(regex))
                .collect(Collectors.toSet());
    }

    //listar diretorios e trazer o map<diretorio, clienteApelido>
    public static Map<Path,String> listByDirectoryDefaultToMap(Path path, String regex) throws IOException{
        Set<Path> paths = filtrarPorDiretorioERegex(path, regex);
        Map<Path,String> parentMap = new HashMap<>();
        paths.forEach(c-> parentMap.put(c,c.getFileName().toString().substring(0,4)));
        return parentMap;
    }

    //buscar por ID nos 4 primeiros caracteres
    public static Optional<Path> buscarPastaPorId(Cliente client, Set<Path> paths){
        return paths
                .stream()
                .filter(n->n.getFileName().toString().substring(0,4).equals(client.getIdFormatado()))
                .findFirst();
    }

    public static boolean verificarSeExiste(Path file){
        return Files.exists(file);
    }
}
