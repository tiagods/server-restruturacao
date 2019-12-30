package com.tiagods.springbootfile;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DelecaoRecursiva {
    @Test
    public void test() throws Exception{
        Path p1 = Paths.get("C:/job/2222");
        deleteFolderIfEmptyRecursive(p1);
        Assert.assertTrue(Files.exists(p1));
    }
    @Test
    public void test2() throws Exception{
        Path p2 = Paths.get("C:/job/2222 - 2");
        deleteFolderIfEmptyRecursive(p2);
        Assert.assertTrue(Files.notExists(p2));
    }
    //deletar de forma recursiva
    public void deleteFolderIfEmptyRecursive(Path path) throws IOException {
        if(Files.isDirectory(path)){
            try {
                File[] files = deleteIfEmpty(path);
                if(files!=null){
                    for (File p : files) {
                        Path dir = p.toPath();
                        deleteFolderIfEmptyRecursive(dir);
                    }
                    //reanalizar
                    deleteIfEmpty(path);
                }
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    public File[] deleteIfEmpty(Path path) throws IOException{
        File[] files = path.toFile().listFiles();
        if(files.length == 0) {
            FileUtils.deleteDirectory(path.toFile());
            return null;
        }
        else return files;
    }
}
