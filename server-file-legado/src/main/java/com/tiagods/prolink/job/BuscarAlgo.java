package com.tiagods.prolink.job;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class BuscarAlgo {
    private static Set<String> filter = new HashSet<String>();

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("E:\\Contabil\\__C L I E N T E S");
        String[] values = new String[]{".DEC",".REC",".SPED"};
        filter.addAll(Arrays.asList(values));
        Iterator<Path> iterator = Files.list(path).iterator();
        percorrer(iterator);

    }
    private static void percorrer(Iterator<Path> iterator) throws IOException{
        while(iterator.hasNext()){
            Path path = iterator.next();
            if(Files.isDirectory(path)) percorrer(Files.list(path).iterator());
            else if(path.getFileName().toString().length()>=3){
                String nome = path.getFileName().toString();
                if (nome.contains(".")) {
                    String extensao = nome.substring(nome.lastIndexOf("."), nome.length());
                    Optional<String> result = filter.stream().filter(c -> c.equalsIgnoreCase(extensao)).findAny();
                    if (result.isPresent()) {
                        salvarRelatorio(path.toString(), result.get());
                        System.out.println(nome);
                    }
                }
                else if(nome.toUpperCase().contains("RECIBO") || nome.toUpperCase().contains("DECLARA") ){
                    salvarRelatorio(path.toString(), "OUTRO");
                    System.out.println(nome);
                }
            }
        }
    }
    private static void salvarRelatorio(String de, String to) throws IOException{
        Path path = Paths.get("result_contabil.csv");
        if(Files.notExists(path)) Files.createFile(path);
        FileWriter fw = new FileWriter(path.toFile(),true);
        fw.write(de+"\t"+to);
        fw.write(System.getProperty("line.separator"));
        fw.flush();
        fw.close();
    }
}
