package com.prolink.job.implementados;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.stream.Collectors;

public class AddNome {
    public static void main(String[] args) throws IOException{
        Path path = Paths.get(System.getProperty("user.dir"),"1 - arquivo.txt");

        if(Files.exists(path)) System.out.println(path);
        String nomeAnterior = path.getFileName().toString();
        int size = 0;
        for(int i=0;i<nomeAnterior.length();i++){
            try {
                Integer.parseInt(String.valueOf(nomeAnterior.charAt(i)));
                size++;
            }catch (NumberFormatException e){//em caso de erro nao fazer nada
                break;
            }
        }
        if(size>0 && size<4) {
            String novoNome = size == 1 ? "000" : (size == 2 ? "00" : (size == 3 ? "0" : ""));
            Path path2 = Paths.get(path.getParent().toString(), novoNome + nomeAnterior);
            Files.copy(path,path2, StandardCopyOption.COPY_ATTRIBUTES);
            System.out.println("copiado");
        }
    }
}
