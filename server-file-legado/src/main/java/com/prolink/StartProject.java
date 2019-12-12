package com.prolink;

import com.prolink.olders.config.ClienteData;
import com.prolink.olders.dao.ClienteDAO;
import com.prolink.olders.model.Cliente;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StartProject {
    public static void main(String[] args){
        ClienteData data = ClienteData.getInstance();
        ClienteDAO dao = new ClienteDAO();
        data.save(dao.refreshClientes());
        String nome = data.getClientes()
                .stream()
                .map(Cliente::toString)
                .collect(Collectors.joining("\n"));
    }

    @Component
    public static class UtilsMapearDiretorios {
        static Map<Path, List<Path>> map = new HashMap<>();
        static String regexVa = "[0-9]{4}+[^0-9]?";
        static String regex = "[0-9]{4}";


        public static void main(String[] args) throws Exception{
            Path path = Paths.get("\\\\plkserver\\Todos Departamentos\\SAC");
            List<Path> paths = Files.list(path).filter(Files::isDirectory).collect(Collectors.toList());
            for(Path p : paths){
                boolean regexIsOk = p.toFile().getName().matches(regex);
                if(regexIsOk){
                    map.put(p,new ArrayList<>());
                    percorrerDiretorio2(p);
                }
            }

            StringBuilder builder = new StringBuilder();

            List<Path> list = map
                    .values()
                    .stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            list.forEach(c->{
                builder.append(c.getParent().getFileName())
                        .append(";")
                        .append(c.getFileName())
                        .append(System.getProperty("line.separator"));
            });
            System.out.println(builder.toString());

            FileWriter writer = new FileWriter(new File(("relatorio.csv")));
            writer.write(builder.toString());
            writer.flush();
            writer.close();
        }
        public static void percorrerDiretorio(Path path) throws Exception{
            List<Path> paths = Files.list(path).filter(Files::isDirectory).collect(Collectors.toList());
            for(Path p : paths){
                boolean regexIsOk = p.toFile().getName().matches(regex);
                if(regexIsOk){
                    map.get(path).add(p);
                }
            }
        }
        public static void percorrerDiretorio2(Path path) throws Exception{
            List<Path> paths = Files.list(path).filter(Files::isDirectory).collect(Collectors.toList());
            for(Path p : paths){
                boolean regexIsOk = p.toFile().getName().matches(regex);
                if(regexIsOk){
                    try(Stream<Path> walk = Files.walk(p)){
                        Optional<Path> result = walk.filter(c->Files.isRegularFile(c) &&
                                c.toFile().getName().matches(regexVa) &&
                                c.toFile().getName().startsWith("2020")
                        ).findFirst();
                        if(result.isPresent()){
                            map.get(path).add(result.get());
                            break;
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
