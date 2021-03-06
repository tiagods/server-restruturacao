package com.prolink.olders.config;

import com.prolink.olders.model.Cliente;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class BasePath {

    private static Path base = Paths.get("\\\\plkserver\\Clientes");
    //private static Path base = Paths.get("clientes");
    private static Path desligados = base.resolve("_desligados_extintas");
    private static Path modelo = Paths.get(base.toString(),"_base");
    private static Map<Cliente, Path> cliMap = new HashMap<>();

    public BasePath(){
        try {
            Map<Cliente, Path> cliMap = new HashMap<>();

            if (Files.notExists(desligados)) Files.createDirectories(desligados);
            ClienteData clienteData = ClienteData.getInstance();
            Set<Cliente> clienteSet = clienteData.getClientes();

            clienteSet.stream().collect(Collectors.toMap(c->c, null));
            String regex = "[0-9]{4}+[^0-9]?";
            //listando todos os arquivos e corrigir nomes se necessarios
            Set<Path> actives = Files.list(base)
                    .filter(f->Files.isDirectory(f) && f.getFileName().toString()
                            .matches(regex))
                    .collect(Collectors.toSet());

            Set<Path> desl = Files.list(desligados)
                    .filter(f->Files.isDirectory(f) && f.getFileName().toString()
                            .matches(regex))
                    .collect(Collectors.toSet());

            Set<Path> files = new HashSet<>();
            files.addAll(actives);
            files.addAll(desl);

            organizarCliente(clienteSet,files);

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void organizarCliente(Set<Cliente> list,Set<Path> arquivos){
        list.forEach(c->{
            Optional<Path> arquivo = arquivos
                    .stream()
                    .filter(n->n.getFileName().toString().substring(0,4).equals(c.getIdFormatado()))
                    .findFirst();

            if(arquivo.isPresent()) {
                //verificar nome do arquivo se esta de acordo com a norma
                boolean nomeCorreto = arquivo.get().getFileName().toString().equals(c.toString());
                //criar pasta oficial caso não exista
                if (c.getStatus().equalsIgnoreCase("Desligada")) {
                    //caminho do diretorio
                    boolean localCorreto = arquivo.get().getParent().equals(desligados);
                    if(localCorreto && !nomeCorreto) {
                        try{
                            Path destino = desligados.resolve(c.toString());
                            Files.move(arquivo.get(), desligados.resolve(c.toString()), StandardCopyOption.REPLACE_EXISTING);
                            cliMap.put(c, destino);
                        }catch (IOException e){
                            e.printStackTrace();
                            cliMap.put(c,arquivo.get());
                        }
                    }
                    else if(!localCorreto){
                        try {
                            Path destino = desligados.resolve(c.toString());
                            Files.move(arquivo.get(), destino, StandardCopyOption.REPLACE_EXISTING);
                            cliMap.put(c, destino);
                        }catch (IOException e){
                            e.printStackTrace();
                            cliMap.put(c,arquivo.get());
                        }
                    }
                }
                else{
                    boolean localCorreto = arquivo.get().getParent().equals(base);
                    if(localCorreto && !nomeCorreto) {
                        try{
                            Path destino = base.resolve(c.toString());
                            Files.move(arquivo.get(), base.resolve(c.toString()), StandardCopyOption.REPLACE_EXISTING);
                            cliMap.put(c, destino);
                        }catch (IOException e){
                            e.printStackTrace();
                            cliMap.put(c,arquivo.get());
                        }

                    }
                    else if(!localCorreto){
                        try {
                            Path destino = base.resolve(c.toString());
                            Files.move(arquivo.get(), destino, StandardCopyOption.REPLACE_EXISTING);
                            cliMap.put(c, destino);
                        }catch (IOException e){
                            e.printStackTrace();
                            cliMap.put(c,arquivo.get());
                        }
                    }
                }

            }
            else{
                //criar pasta oficial caso não exista
                if(c.getStatus().equalsIgnoreCase("Desligada")) {
                    Path file2 = desligados.resolve(c.toString());
                    if (Files.notExists(file2)) {
                        try {
                            Files.createDirectory(file2);
                            cliMap.put(c,file2);
                        }catch (IOException e){
                            e.printStackTrace();
                            //em caso de erro nenhum diretorio sera criado
                            cliMap.put(c,null);
                        }
                    }
                }
                else{
                    Path file1 = base.resolve(c.toString());
                    if (Files.notExists(file1)) {
                        try {
                            Files.createDirectory(file1);
                            cliMap.put(c,file1);
                        }catch (IOException e){
                            //em caso de erro nenhum diretorio sera criado
                            cliMap.put(c,null);
                        }
                    }
                }
            }
        });
    }

    public Path buscarCliente(Cliente c) {
        return cliMap.get(c);
        /*
        Path file1 = base.resolve(c.toString());
        if (c.getStatus().equalsIgnoreCase("Desligada")) {
            Path file2 = desligados.resolve(c.toString());
            return Files.notExists(file2) ? null : file2;
        } else
            return Files.notExists(file1) ? null : file1;
        */
    }
    public void verificarEstrutura(Path estrutura) {
        try {
            Path path = modelo.resolve(estrutura);
            if (Files.notExists(path)) Files.createDirectories(path);
        }catch (IOException e){
            System.out.print("Falha ao criar estrutura : "+e.getMessage());
        }
    }

    public static Path getBase() {
        return base;
    }

    public static Path getDesligados() {
        return desligados;
    }

    public static Path getModelo() {
        return modelo;
    }

    public boolean cnpjIsValid(String cnpj){
        String cnpjFormat = "(^\\d{2}\\d{3}\\d{3}\\d{4}\\d{2}$)";
        Matcher matcher = Pattern.compile(cnpjFormat).matcher(cnpj);
        return matcher.find();
    }


}
