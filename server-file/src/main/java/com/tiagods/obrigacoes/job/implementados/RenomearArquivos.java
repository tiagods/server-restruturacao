package com.tiagods.obrigacoes.job.implementados;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RenomearArquivos {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get(
                        "E:\\Obrigacoes\\contabil\\_old - obrigações de anos anteriores 2014\\DIRF 2015\\ARQUIVOS PARA IMPORTAÇÃO\\FISCAL");
        File[] files = path.toFile().listFiles();
        for(File f : files){
            Path origem = f.toPath();
            Path destino = path.resolve(origem.getFileName().toString().replace("_02","_2"));
            Files.move(origem,destino);
            System.out.println("origem > " +origem+"\tDestino >"+destino);
        }
    }

    public void renomearEmCascata(){

    }
}
