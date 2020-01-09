package com.tiagods.prolink.service;

import com.tiagods.prolink.config.Regex;
import com.tiagods.prolink.config.ServerFile;
import com.tiagods.prolink.dto.ClientDefaultPathDTO;
import com.tiagods.prolink.exception.StructureNotFoundException;
import com.tiagods.prolink.model.Pair;
import com.tiagods.prolink.model.Cliente;
import com.tiagods.prolink.utils.IOUtils;
import com.tiagods.prolink.dto.ClienteDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class ClientIOService {

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

    //cliente e sua localizacao
    private Map<Cliente, Path> cliMap = new HashMap<>();

    private Set<Cliente> clientSet = new HashSet<>();

    private List<ClienteDTO> clientDTOList = new ArrayList<>();

    private Set<Path> foldersConcurrentJobs = Collections.synchronizedSet(new HashSet<>());

    @Autowired
    private ClientService clientService;

    private void createIsEmpty(){
        if(clientSet.isEmpty() || cliMap.isEmpty()) initClientsPaths(false);
    }

    void destroyAll() {
        clientSet.clear();
        clientDTOList.clear();
        cliMap.clear();
        foldersConcurrentJobs.clear();
    }

    @Async
    public synchronized void initClientsPaths(boolean organize) throws StructureNotFoundException{
        destroyAll();
        //carregar e converter lista de clientes
        clientDTOList = clientService.list();
        clientDTOList.forEach(c -> {
            Cliente cli = new Cliente(c.getApelido(), c.getNome(), c.getStatus(), c.getCnpj());
            clientSet.add(cli);
        });
        log.info("Iniciando mapeamento de clientes");
        Set<Path> set = listAllInBaseAndShutdown();
        clientSet.forEach(c -> {
            mapClient(c,set,organize);
        });
        log.info("Concluido mapeamento: "+cliMap.values().size()+" pastas de clientes mapeadas");
    }

    //listar todos os clientes ativos, inativos e suas pastas
    private synchronized Set<Path> listAllInBaseAndShutdown() throws StructureNotFoundException{
        verifyFoldersInBase();
        try {
            //listando todos os arquivos e corrigir nomes se necessarios
            Set<Path> actives = ioUtils.listByDirectoryAndRegex(base, regex.getInitById());
            Set<Path> shutdowns = ioUtils.listByDirectoryAndRegex(shutdown, regex.getInitById());
            Set<Path> files = new HashSet<>();
            files.addAll(actives);
            files.addAll(shutdowns);
            return files;
        } catch (IOException e) {
            throw new StructureNotFoundException("Nao foi possivel listar os arquivos dos clientes", e.getCause());
        }
    }

    //mapeamento de pastas, cuidado ao usar em produção
    private void mapClient(Cliente c, Set<Path> files, boolean organize) {
        log.info("Mapeando cliente: "+c.toString());
        Optional<Path> file = ioUtils.searchFolderById(c, files);
        Optional<ClienteDTO> opt = clientDTOList.stream().filter(f-> f.getApelido().equals(c.getId())).findFirst();
        //verificar se ja foi criado
        boolean isCreated = opt.map(ClienteDTO::isFolderCreate).orElse(true);

        Pair<Cliente, Path> pair;
        Path destinoDesligada = shutdown.resolve(c.toString());
        Path destinoAtiva = base.resolve(c.toString());

        if (file.isPresent()) {
            //verificar nome do arquivo se esta de acordo com a norma
            boolean correctName = file.get().getFileName().toString().equals(c.toString());
            //caminho do diretorio
            if(organize) {
                if (c.getStatus().equalsIgnoreCase("Desligada"))
                    pair = moveFolder(c, file.get(), shutdown, correctName, destinoDesligada);
                else pair = moveFolder(c, file.get(), base, correctName, destinoAtiva);
            }
            else pair = new Pair<>(c,file.get());
        }
        //criar pasta apenas em condicao de que deva ser criado, principalmente em novos clientes
        else if (!isCreated) {
            //criar pasta oficial caso não exista
            if (c.getStatus().equalsIgnoreCase("Desligada")) pair = createFolder(c,destinoDesligada);
            else pair = createFolder(c, destinoAtiva);

            Optional<ClienteDTO> dtoOptional = clientService.findOne(c.getId());
            if(dtoOptional.isPresent()){
                ClienteDTO cli = dtoOptional.get();
                cli.setFolderCreate(true);
                clientService.save(cli);
            }
        }
        else pair = new Pair<>(c, null);
        cliMap.put(pair.getCliente(), pair.getPath());
    }

    private Pair<Cliente, Path>  createFolder(Cliente c, Path path){
        Pair<Cliente, Path> pair = ioUtils.create(c, path);
        log.info("Criado pasta: "+path.toString());
        if(Files.exists(path)){
            //usado para criar uma estrutura basica de um novo cliente
            List<ClientDefaultPathDTO> paths = clientService.getPathsForClient();
            for (ClientDefaultPathDTO pathDTO : paths) {
                try{
                    Path p = path.resolve(pathDTO.getValue());
                    ioUtils.createDirectories(p);
                }catch (IOException e){
                    log.error("Falha ao criar estrutura basica "+e.getMessage());
                }
            }
        }
        return pair;
    }

    private Pair<Cliente, Path> moveFolder(Cliente c, Path file, Path base, boolean correctName, Path destiny){
        boolean localCorreto = file.getParent().equals(base);
        if (!localCorreto || !correctName) {
            log.info("Movendo cliente: " + c.getIdFormatado() + " de: " + file.toString() + " para: " + destiny.toString());
            return ioUtils.move(c, file, destiny);
        } else return new Pair<>(c, destiny);
    }

    public Path searchClientPathBaseAndCreateIfNotExists(Cliente c) {
        createIsEmpty();
        Optional<Path> result = Optional.ofNullable(cliMap.get(c));
        if(result.isPresent()) return result.get();
        else {
            Path p = base.resolve(c.toString());
            Optional<Pair<Cliente,Path>> result2 = Optional.ofNullable(ioUtils.create(c, p));
            if(result2.isPresent()){
                cliMap.put(c, result2.get().getPath());
                return result2.get().getPath();
            }
            else
                throw new StructureNotFoundException("Não foi possivel criar o diretorio: " + p.toString());
        }
    }
    public Path searchClientPathBase(Cliente c) {
        createIsEmpty();
        return cliMap.get(c);
    }
    public Path searchClientPathBaseById(Long id) {
        Optional<Path> optional = findMapClientById(id)
                .map(this::searchClientPathBase);
        return optional.orElse(null);
    }
    private Path searchClientPathBaseByIdAndCreate(Long id) {
        return findMapClientById(id)
                .map(this::searchClientPathBaseAndCreateIfNotExists).get();
    }
    public Optional<Cliente> findMapClientById(long id){
        createIsEmpty();
        return cliMap
                .keySet()
                .stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }
    //verificar e criar estrutura de modelo
    public void verifyStructureInModel(Path structure) throws IOException {
        Path path = getModel().resolve(structure);
        ioUtils.createDirectories(path);
    }

    public void verifyFoldersInBase() {
        base = Paths.get(serverFile.getBase());
        shutdown = Paths.get(serverFile.getShutdown());
        model = Paths.get(serverFile.getModel());
        log.info("Verificando se pastas padroes se existem");
        try {
            if (!ioUtils.verifyIfExist(base))
                throw new StructureNotFoundException("Base de arquivos não existe");
            ioUtils.createDirectory(shutdown);
            ioUtils.createDirectory(model);
            log.info("Concluindo verificação");
        } catch (IOException e) {
            log.error("Falha ao verificar/criar diretorios");
            throw new StructureNotFoundException("Pasta's importantes não fora encontradas: "
                    + e.getMessage(), e.getCause());
        }
    }
    public Path getModel() {
        return this.model;
    }

    public void addFolderToJob(Path dirForJob) {
        this.foldersConcurrentJobs.add(dirForJob);
    }
    public void removeFolderToJob(Path job) {
        this.foldersConcurrentJobs.remove(job);
    }
    public boolean containsFolderToJob(Path job) {
        return this.foldersConcurrentJobs.contains(job);
    }
}
