package com.prolink.olders.config;

import com.prolink.model.Pair;
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

    private BasePath(){
        try {

            ClienteData clienteData = ClienteData.getInstance();
            Set<Cliente> clienteSet = clienteData.getClientes();

            organizarCliente(clienteSet,files);

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void organizarCliente(Set<Cliente> list,Set<Path> arquivos){
         Map<Cliente, Path> cliMap = new HashMap<>();
         Path base = null;
         Path desligados= null;
         Path modelo= null;

        list.forEach(c->{
            Optional<Path> arquivo = arquivos
                    .stream()
                    .filter(n->n.getFileName().toString().substring(0,4).equals(c.getIdFormatado()))
                    .findFirst();

            Pair pair;

            if(arquivo.isPresent()) {
                //verificar nome do arquivo se esta de acordo com a norma
                boolean nomeCorreto = arquivo.get().getFileName().toString().equals(c.toString());
                //caminho do diretorio
                if (c.getStatus().equalsIgnoreCase("Desligada")) {
                    boolean localCorreto = arquivo.get().getParent().equals(desligados);
                    if(!localCorreto || !nomeCorreto) pair = mover(c, arquivo.get(), destinoDesligada);
                }
                else{
                    boolean localCorreto = arquivo.get().getParent().equals(base);
                    if(!localCorreto || !nomeCorreto) pair = mover(c, arquivo.get(), destinoAtiva);
                }

            }
            else{
                //criar pasta oficial caso não exista
                if(c.getStatus().equalsIgnoreCase("Desligada")) pair = criar(c, destinoDesligada);
                else pair = criar(c,destinoAtiva);
            }
        });
    }
}
