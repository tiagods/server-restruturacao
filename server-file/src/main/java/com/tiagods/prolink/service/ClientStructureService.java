package com.tiagods.prolink.service;

import com.tiagods.prolink.config.Regex;
import com.tiagods.prolink.config.ServerFile;
import com.tiagods.prolink.exception.StructureNotFoundException;
import com.tiagods.prolink.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@Service
public class StructureService {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ServerFile serverFile;
    @Autowired
    private Regex regex;
    @Autowired
    private IOUtils ioUtils;

    private Path base;
    private Path shutdown;
    private Path model;

    @PostConstruct
    private void init(){
        verifyFolders();
    }
    //listar todos os clientes ativos, inativos e suas pastas

    public Set<Path> listAllInBaseAndShutdown(){
        try {
            //listando todos os arquivos e corrigir nomes se necessarios
            Set<Path> actives = ioUtils.listByDirectoryAndRegex(base, regex.getInitById());
            Set<Path> shutdowns = ioUtils.listByDirectoryAndRegex(shutdown, regex.getInitById());
            Set<Path> files = new HashSet<>();
            files.addAll(actives);
            files.addAll(shutdowns);
            return files;
        }catch (IOException e){
            throw new StructureNotFoundException("Nao foi possivel listar os arquivos dos clientes",e.getCause());
        }
    }
    private void verifyFolders(){
        base  = Paths.get(serverFile.getBase());
        shutdown = Paths.get(serverFile.getShutdown());
        model = Paths.get(serverFile.getModel());
        log.info("Verificando se pastas padroes se existem");
        try {
            if (!ioUtils.verifyIfExist(base))
                throw new StructureNotFoundException("Base de arquivos não existe");
            ioUtils.createDirectory(shutdown);
            ioUtils.createDirectory(model);
            log.info("Concluindo verificação");
        } catch (StructureNotFoundException e){
            log.error("Falha ao verificar/criar diretorios");
            throw new RuntimeException("Pasta's importantes não fora encontradas: "
                    +e.getMessage(),e.getCause());
        }
    }
    public Path getModel() {
        return model;
    }
    public Path getShutdown() {
        return shutdown;
    }
    public Path getBase() {
        return base;
    }
}
