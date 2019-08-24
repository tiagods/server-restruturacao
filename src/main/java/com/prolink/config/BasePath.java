package com.prolink.config;

import com.prolink.model.Cliente;

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
            if (Files.notExists(desligados)) Files.createDirectories(desligados);
            ClienteData clienteData = ClienteData.getInstance();
            Set<Cliente> clienteSet = clienteData.getClientes();
            final String numberApelido = "[0-9]{4}";
            //listando todos os arquivos e corrigir nomes se necessarios
            Set<Path> files = Files.list(base).filter(f->f.getFileName().toString().contains("-") &&
                    Pattern.compile(numberApelido).matcher(f.getFileName().toString().substring(0,4)).find()).collect(Collectors.toSet());
            Set<Path> des = Files.list(desligados).filter(f->f.getFileName().toString().contains("-") &&
                    Pattern.compile(numberApelido).matcher(f.getFileName().toString().substring(0,4)).find()).collect(Collectors.toSet());
            files.addAll(des);
            for(Path p : files){
                String nome = p.getFileName().toString();
                Optional<Cliente> cliente = clienteSet.stream().filter(c->c.getIdFormatado().equals(nome.substring(0,4))).findFirst();
                if(cliente.isPresent()){
                    Cliente cli = cliente.get();
                    Path novoCaminho = cli.getStatus().equalsIgnoreCase("Desligada")?desligados.resolve(cli.toString()):base.resolve(cli.toString());
                    if(!nome.equals(cli.toString())) Files.move(p,novoCaminho,StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
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

    public void organizarCliente(Cliente c) throws IOException{

        Path file1 = base.resolve(c.toString());
        if (c.getStatus().equalsIgnoreCase("Desligada")) {
            Path file2 = desligados.resolve(c.toString());
            try {
                if (Files.exists(file1)) Files.move(file1, file2, StandardCopyOption.REPLACE_EXISTING);
                else if (Files.notExists(file2)) {
                    Files.createDirectory(file2);
                }
                cliMap.put(c, file2);
            }catch (IOException e){
                if(Files.notExists(file1))
                    Files.createDirectory(file1);
                cliMap.put(c,file1);
            }
        } else if (Files.notExists(file1)) {
            Files.createDirectory(file1);
            cliMap.put(c, file1);
        }
    }

    public Path buscarCliente(Cliente c) {
        Path file1 = base.resolve(c.toString());
        if (c.getStatus().equalsIgnoreCase("Desligada")) {
            Path file2 = desligados.resolve(c.toString());
            return Files.notExists(file2) ? null : file2;
        } else
            return Files.notExists(file1) ? null : file1;
    }
    public void verificarEstrutura(Path estrutura) {
        try {
            Path path = modelo.resolve(estrutura);
            if (Files.notExists(path)) Files.createDirectories(path);
        }catch (IOException e){
            System.out.print("Falha ao criar estrutura : "+e.getMessage());
        }
    }
}
