package com.tiagods.obrigacoes.job;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RenomearPasta {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("\\\\PLKSERVER\\Obrigacoes\\contabil\\Contabil\\SPED ICMS IPI\\2019\\02");
        File[] files = path.toFile().listFiles();
        for(File f : files){
            Path origem = f.toPath();
            Path destino = path.resolve(origem.getFileName().toString().replace("_2018",""));
            Files.move(origem,destino);
            System.out.println("origem > " +origem+"\tDestino >"+destino);
        }
    }
}
