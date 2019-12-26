package com.tiagods.prolink.service;

import com.tiagods.prolink.exception.StructureNotFoundException;
import com.tiagods.prolink.model.Pair;
import com.tiagods.prolink.model.Cliente;
import com.tiagods.prolink.utils.IOUtils;
import com.tiagods.prolink.dto.ClienteDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.util.*;

@Service
public class ClientIOService {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private IOUtils ioUtils;

    @Autowired
    private ClientStructureService structureService;
    //cliente e sua localizacao
    private Map<Cliente, Path> cliMap = new HashMap<>();

    private Set<Cliente> allClients = new HashSet<>();

    @Autowired
    private ClienteService clienteService;

    @PostConstruct
    public void onInit(){
        //carregar e converter lista de clientes
        List<ClienteDTO> list = clienteService.list();
        list.forEach(c->{
            Cliente cli = new Cliente(c.getApelido(),c.getNome(),c.getStatus(),c.getCnpj());
            allClients.add(cli);
        });
        log.info("Iniciando mapeamento de clientes");
        mapClient(structureService.listAllInBaseAndShutdown());
        log.info("Concluindo mapeamento");
    }
    //mapeamento de pastas, cuidado ao usar em produção
    public void mapClient(Set<Path> files){
        allClients.forEach(c->{
            Optional<Path> file = ioUtils.searchFolderById(c,files);

            Optional<ClienteDTO> opt = clienteService.findOne(c.getId());
            //verificar se ja foi criado
            boolean isCreated = opt.map(ClienteDTO::isFolderCreate).orElse(true);

            Pair<Cliente,Path> pair;

            Path desligados = structureService.getShutdown();
            Path base = structureService.getBase();

            Path destinoDesligada = structureService.getShutdown().resolve(c.toString());
            Path destinoAtiva = structureService.getBase().resolve(c.toString());

            if(file.isPresent()) {
                //verificar nome do arquivo se esta de acordo com a norma
                boolean nomeCorreto = file.get().getFileName().toString().equals(c.toString());
                //caminho do diretorio
                if (c.getStatus().equalsIgnoreCase("Desligada")) {
                    boolean localCorreto = file.get().getParent().equals(desligados);
                    if(!localCorreto || !nomeCorreto) pair = ioUtils.move(c, file.get(), destinoDesligada);
                    else pair = new Pair<>(c,destinoDesligada);
                }
                else{
                    boolean localCorreto = file.get().getParent().equals(base);
                    if(!localCorreto || !nomeCorreto) pair = ioUtils.move(c, file.get(), destinoAtiva);
                    else pair = new Pair<>(c,destinoAtiva);
                }
            }
            //criar pasta apenas em condicao de que deva ser criado
            else if(!isCreated){
                //criar pasta oficial caso não exista
                if(c.getStatus().equalsIgnoreCase("Desligada")) pair = ioUtils.create(c, destinoDesligada);
                else pair = ioUtils.create(c,destinoAtiva);
            }
            else{
                pair = new Pair<>(c,null);
            }
            cliMap.put(pair.getCliente(),pair.getPath());
        });
    }
    public Path searchClientPathBase(Cliente c) {
        Path p =  cliMap.get(c);
        if (p == null) {
            p = structureService.getBase().resolve(c.toString());
            Pair pair = ioUtils.create(c,p);
            if(pair.getPath() == null)
                throw new StructureNotFoundException("Não foi possivel criar o diretorio: "+p.toString());
            else{
                cliMap.put(c,p);
            }
        }
        return p;

    }
    public Path searchClientPathBaseById(Long id) {
        return cliMap
                .keySet()
                .stream()
                .filter(c-> c.getId().equals(id))
                .findFirst()
                .map(this::searchClientPathBase).orElse(null);
    }

    public Optional<Cliente> searchClientById(Long id){
        return allClients.parallelStream().filter(f-> f.getId().equals(id)).findFirst();
    }
    public Set<Cliente> getAllClients() {
        return allClients;
    }
}
