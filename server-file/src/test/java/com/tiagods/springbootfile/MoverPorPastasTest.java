package com.tiagods.springbootfile;

import com.tiagods.prolink.model.Cliente;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MoverPorPastasTest {
    public static void main(String[] args) throws IOException {
        Path job = Paths.get("c:/job");

        System.out.println("2225-CLIENTE B IND SA".matches("[2225]+[^0-9]*$"));
        System.out.println("2222-".matches("2225+[^0-9]*$"));

        Map<Path,String> mapClientes = listByDirectoryDefaultToMap(job, "[0-9]{4}+[^0-9]*$");
        mapClientes.keySet().forEach(c->System.out.println(c.toString()+" - "+mapClientes.get(c)));
    }
    public static Map<Path,String> listByDirectoryDefaultToMap(Path path, String regex) throws IOException {
        Set<Path> paths = listByDirectoryAndRegex(path, regex);
        Map<Path,String> parentMap = new HashMap<>();
        paths.forEach(c-> parentMap.put(c,c.getFileName().toString().substring(0,4)));
        return parentMap;
    }
    //listar diretorios e por regex
    public static Set<Path> listByDirectoryAndRegex(Path path, String regex) throws IOException{
        return Files.list(path)
                .filter(f->Files.isDirectory(f) && f.getFileName().toString().matches(regex))
                .collect(Collectors.toSet());
    }
}
