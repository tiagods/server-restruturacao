package com.prolink.service;

import com.prolink.exception.StructureNotFoundException;
import com.prolink.olders.model.Cliente;
import com.prolink.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class StructureServiceImpl implements StructureService{

    @Value("${fileServer.regex}") private String regex;
    @Value("${fileServer.base}") private String sBase;
    @Value("${fileServer.model}") private String sModel;
    @Value("${fileServer.shutdown}") private String sShutdown;

    @Autowired
    private IOUtils ioUtils;

    private Path base;
    private Path shutdown;
    private Path model;

    @PostConstruct
    private void init(){
        base  = Paths.get(sBase);
        shutdown = Paths.get(sShutdown);
        model = Paths.get(sModel);

        ioUtils.createDirectory(shutdown);
        ioUtils.createDirectory(model);

        listAllInBaseAndShutdown();

    }
    //listar todos os clientes ativos e inativos, e suas pastas
    private void listAllInBaseAndShutdown(){
        Set<Cliente> clientSet = new HashSet<>();

        try {
            //listando todos os arquivos e corrigir nomes se necessarios
            Set<Path> actives = ioUtils.listByDirectoryAndRegex(base, regex);
            Set<Path> desl = ioUtils.listByDirectoryAndRegex(shutdown, regex);

            Set<Path> files = new HashSet<>();
            files.addAll(actives);
            files.addAll(desl);

            organizarCliente(clienteSet,files);
        }catch (IOException e){
            throw new StructureNotFoundException("Nao foi possivel listar os arquivos dos clientes",e.getCause());
        }
    }


    @Override
    public Path getBase() {
        return base;
    }
    @Override
    public Path getModel() {
        return model;
    }
    @Override
    public Path getShutdown() {
        return shutdown;
    }
}
