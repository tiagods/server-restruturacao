package com.tiagods.obrigacoes.job;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

public class ObrigacaoFolder {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("E:\\Obrigacoes\\Controle\\RAIS");
        Set<Path> set = Files.list(path).collect(Collectors.toSet());
        for(Path p : set){
            Path newFile = Paths.get(p.getParent().toString(), p.getFileName().toString().replace("Rais ",""));
            Files.move(p,newFile);
        }
    }
}
