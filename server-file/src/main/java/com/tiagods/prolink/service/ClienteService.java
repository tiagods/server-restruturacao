package com.tiagods.prolink.service;

import com.tiagods.prolink.config.Regex;
import com.tiagods.prolink.config.ServerFile;
import com.tiagods.prolink.dto.ClientDefaultPathDTO;
import com.tiagods.prolink.exception.EstruturaNotFoundException;
import com.tiagods.prolink.model.Pair;
import com.tiagods.prolink.model.Cliente;
import com.tiagods.prolink.utils.IOUtils;
import com.tiagods.prolink.dto.ClienteDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClienteService {

    @Autowired private ServerFile serverFile;
    @Autowired private Regex regex;
    @Autowired private ClienteDAOService clienteDAOService;
    @Autowired private IOService ioService;

    private Path base;
    private Path shutdown;
    private Path model;

    //cliente e sua localizacao
    private Map<Cliente, Path> cliMap = new LinkedHashMap<>();

    private Set<Cliente> clientSet = new LinkedHashSet<>();

    private List<ClienteDTO> clientDTOList = new LinkedList<>();

    private Set<Path> foldersConcurrentJobs = Collections.synchronizedSet(new HashSet<>());

    private void iniciarlizarSeVazio(){
        if(clientSet.isEmpty() || cliMap.isEmpty()) inicializarPathClientes(null, false, false);
    }

    void destroyAll() {
        clientSet.clear();
        clientDTOList.clear();
        cliMap.clear();
        foldersConcurrentJobs.clear();
    }

    public synchronized void inicializarPathClientes(ClienteDTO cliente, boolean organizar, boolean forcarCriacao) throws EstruturaNotFoundException {
        destroyAll();
        //carregar e converter lista de clientes
        if(cliente!=null) {
            clientDTOList.add(cliente);
        } else {
            clientDTOList = clienteDAOService.list();
        }
        clientDTOList.forEach(c -> {
            clientSet.add(
                    new Cliente(c.getApelido(), c.getNome(), c.getStatus(), c.getCnpj())
            );
        });
        log.info("Iniciando mapeamento de clientes");
        Set<Path> set = buscarClientesPath();
        clientSet.forEach(c -> {
            mapClient(c, set, organizar, forcarCriacao);
        });
        log.info("Concluido mapeamento: "+cliMap.values().size()+" pastas de clientes mapeadas");
    }

    //listar todos os clientes ativos, inativos e suas pastas
    private synchronized Set<Path> buscarClientesPath() throws EstruturaNotFoundException {
        verficarDiretoriosBaseECriar();
        try {
            //listando todos os arquivos e corrigir nomes se necessarios
            Set<Path> actives = IOUtils.filtrarPorDiretorioERegex(base, regex.getInitById());
            Set<Path> shutdowns = IOUtils.filtrarPorDiretorioERegex(shutdown, regex.getInitById());
            Set<Path> files = new HashSet<>();
            files.addAll(actives);
            files.addAll(shutdowns);
            return files;
        } catch (IOException e) {
            throw new EstruturaNotFoundException("Nao foi possivel listar os arquivos dos clientes", e.getCause());
        }
    }

    //mapeamento de pastas
    private void mapClient(Cliente c, Set<Path> files, boolean organizar, boolean forcarCreate) {
        Optional<Path> file = IOUtils.buscarPastaPorId(c, files);
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
            if(organizar) {
                if (c.getStatus().equalsIgnoreCase("Desligada"))
                    pair = moverPastaCliente(c, file.get(), shutdown, correctName, destinoDesligada);
                else pair = moverPastaCliente(c, file.get(), base, correctName, destinoAtiva);
            }
            else pair = new Pair<>(c,file.get());
        }
        //criar pasta apenas em condicao de que deva ser criado, principalmente em novos clientes
        else if (forcarCreate || !isCreated) {
            //criar pasta oficial caso não exista
            if (c.getStatus().equalsIgnoreCase("Desligada")) {
                pair = montarEstruturaNoCliente(c,destinoDesligada);
            } else {
                pair = montarEstruturaNoCliente(c, destinoAtiva);
            }
            Optional<ClienteDTO> dtoOptional = clienteDAOService.findOne(c.getId());
            if(dtoOptional.isPresent()){
                ClienteDTO cli = dtoOptional.get();
                cli.setFolderCreate(true);
                clienteDAOService.save(cli);
            }
        }
        else pair = new Pair<>(c, null);
        cliMap.put(pair.getCliente(), pair.getPath());
    }

    //criar estrutura no cliente com base nos parametros passados
    private Pair<Cliente, Path> montarEstruturaNoCliente(Cliente c, Path path){
        Pair<Cliente, Path> pair = IOUtils.criarDiretorioCliente(c, path);
        log.info("Criado pasta: "+path.toString());
        if(Files.exists(path)) {
            // usado para criar uma estrutura basica
            // de um novo cliente com pastas basicas como GERAL, FISCAL, CONTABIL
            List<ClientDefaultPathDTO> paths = clienteDAOService.listarPastasPadroes();
            for (ClientDefaultPathDTO pathDTO : paths) {
                try{
                    Path p = path.resolve(pathDTO.getValue());
                    IOUtils.criarDiretorios(p);
                }catch (IOException e){
                    log.error("Falha ao criar estrutura basica "+e.getMessage());
                }
            }
        }
        return pair;
    }

    private Pair<Cliente, Path> moverPastaCliente(Cliente c, Path file, Path base, boolean nomeCorreto, Path destino){
        boolean localCorreto = file.getParent().equals(base);
        if (!localCorreto || !nomeCorreto) {
            log.info("Movendo cliente: [" + c.getIdFormatado() + "] de: [" + file.toString() + "] para: [" + destino.toString()+"]");
            return ioService.mover(c, file, destino);
        } else return new Pair<>(c, destino);
    }

    public Path buscarPastaDoClienteECriarSeNaoExistir(Cliente c) {
        iniciarlizarSeVazio();
        Optional<Path> result = Optional.ofNullable(cliMap.get(c));
        if(result.isPresent()) return result.get();
        else {
            Path p = base.resolve(c.toString());
            Optional<Pair<Cliente,Path>> result2 = Optional.ofNullable(IOUtils.criarDiretorioCliente(c, p));
            if(result2.isPresent()){
                cliMap.put(c, result2.get().getPath());
                return result2.get().getPath();
            }
            else
                throw new EstruturaNotFoundException("Não foi possivel criar o diretorio: " + p.toString());
        }
    }

    public Path buscarPastaBaseCliente(Cliente c) {
        iniciarlizarSeVazio();
        return cliMap.get(c);
    }

    public Path buscarPastaBaseClientePorId(Long id) {
        Optional<Path> optional = buscarClienteEmMapPorId(id)
                .map(this::buscarPastaBaseCliente);
        return optional.orElse(null);
    }

    public Set<Path> listarDiretorios(Path path) throws IOException{
        Set<Path> list = new LinkedHashSet<>();
        list.add(path);
        Files.list(path)
//                .filter(Files::isDirectory)
                .forEach(list::add);
        return list;
    }

    public Optional<Cliente> buscarClienteEmMapPorId(long id){
        iniciarlizarSeVazio();
        return cliMap
                .keySet()
                .stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    //verificar e criar estrutura de modelo
    public void verificarEstruturaNoModelo(Path estrutura) throws IOException {
        Path path = getModel().resolve(estrutura);
        IOUtils.criarDiretorios(path);
    }

    public void verficarDiretoriosBaseECriar() {
        base = Paths.get(serverFile.getBase());
        shutdown = Paths.get(serverFile.getShutdown());
        model = Paths.get(serverFile.getModel());
        log.info("Verificando se pastas padroes se existem");
        try {
            if (!IOUtils.verificarSeExiste(base))
                throw new EstruturaNotFoundException("Base de arquivos não existe");
            IOUtils.criarDiretorio(shutdown);
            IOUtils.criarDiretorio(model);
            log.info("Concluindo verificação");
        } catch (IOException e) {
            log.error("Falha ao verificar/criar diretorios");
            throw new EstruturaNotFoundException("Pasta's importantes não fora encontradas: "
                    + e.getMessage(), e.getCause());
        }
    }

    public Path getModel() {
        return this.model;
    }

    public void addFolderToJob(Path dirForJob) {
        this.foldersConcurrentJobs.add(dirForJob);
    }
    public void removeFolderToJob(Path job) { this.foldersConcurrentJobs.remove(job); }
    public boolean containsFolderToJob(Path job) {
        return this.foldersConcurrentJobs.contains(job);
    }
}
