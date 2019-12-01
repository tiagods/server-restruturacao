package com.prolink.job;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

public class ZiparDiretorios {
    public static void main(String[] args) {
        new ZiparDiretorios().start();
    }
    public void start(){
        Path path = Paths.get("E:\\Obrigacoes\\contabil\\_old - obrigações de anos anteriores 2014\\desligamentos");
        Path path2 = Paths.get("E:\\Obrigacoes\\contabil\\_old - obrigações de anos anteriores 2014\\desligamentos_backup");
        try{
            Iterator<Path> iterator = Files.list(path).iterator();

            if(Files.notExists(path2)) Files.createDirectory(path2);

            while(iterator.hasNext()){
                Path f = iterator.next();
                Path zipFile = path2.resolve(f.getFileName()+".zip");

                if(Files.isDirectory(f) && Files.notExists(zipFile)){
                    ZipFolder zip = new ZipFolder();
                    zip.zipFolder(f.toString(),zipFile.toString());

                    System.out.println("File> "+f.toString()+"\tto\t"+zipFile.toString());
                }
            }
            System.out.print(path.getParent());

        }catch (IOException e){
            e.printStackTrace();
        }

    }
}
