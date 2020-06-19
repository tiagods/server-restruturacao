package com.tiagods.prolink;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class PrintSubpaths {

    public static void main(String[] args) throws IOException{
        String regex = "[0-9]{4}+[^0-9]*$";

        Path path = Paths.get("\\\\plkserver\\Todos Departamentos\\SAC");
        Iterator<Path> iterator = Files.list(path).filter(f-> Files.isDirectory(f) && f.getFileName().toString().matches(regex)).iterator();
        Map<String, List<String>> maps = new HashMap<>();

        while(iterator.hasNext()){
            Path p = iterator.next();
            String fileName = p.getFileName().toString();
            String cli = fileName.substring(0,4);
            List<String> collect = Files.list(p)
                    .filter(f ->  Files.isDirectory(f) && f.getFileName().toString().matches(regex))
                    .map(f->f.getFileName().getFileName().toString().substring(0,4))
                    .collect(Collectors.toList());
            if(!collect.isEmpty())
                maps.put(cli,collect);
        }

        StringBuilder sb = new StringBuilder();
        maps.keySet().forEach(c->{
            maps.get(c).forEach(f->{
                sb.append(c).append(";").append(f)
                        .append(System.getProperty("line.separator"));
            });
        });
        FileWriter writer = new FileWriter(new File("result.csv"));
        writer.write(sb.toString());
        writer.flush();
        writer.close();

    }
}
