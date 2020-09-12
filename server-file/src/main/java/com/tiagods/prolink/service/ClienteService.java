package com.tiagods.prolink.service;

import com.tiagods.prolink.config.Regex;
import com.tiagods.prolink.config.ServerFile;
import com.tiagods.prolink.dao.ClienteDAOService;
import com.tiagods.prolink.dto.ClientDefaultPathDTO;
import com.tiagods.prolink.exception.EstruturaNotFoundException;
import com.tiagods.prolink.io.IOService;
import com.tiagods.prolink.model.Pair;
import com.tiagods.prolink.model.Cliente;
import com.tiagods.prolink.utils.IOUtils;
import com.tiagods.prolink.dto.ClienteDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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

    private Set<Path> foldersConcurrentJobs = Collections.synchronizedSet(new LinkedHashSet<>());

    private void iniciarlizarSeVazio(String cid){
        if(clientSet.isEmpty() || cliMap.isEmpty()) inicializarPathClientes(cid, null, false, false);
    }

    void destroyAll(String cid) {
        log.info("Correlation: [{}] Limpando listas", cid);
        clientSet.clear();
        clientDTOList.clear();
        cliMap.clear();
        foldersConcurrentJobs.clear();
    }

    public synchronized void inicializarPathClientes(String cid, ClienteDTO cliente, boolean organizar, boolean forcarCriacao) throws EstruturaNotFoundException {
        destroyAll(cid);

        Map<String, Object> parametros = new HashMap<>(){{
            put("cliente", cliente!=null?cliente.getId():null);
            put("organizar",organizar);
            put("forcarCriacao", forcarCriacao);
        }};

        log.info("Correlation: [{}] Inicializando mapeamento de clientes. Parametros : {}", cid, parametros);

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
        log.info("Correlation: [{}]. Inicializando mapeamento de clientes. Parametros : {}", cid, parametros);

        log.info("Correlation: [{}]. Iniciando mapeamento de clientes", cid);
        Set<Path> set = listarPastasNaBase(cid);

        synchronized (cliMap) {
            clientSet.forEach(c -> {
                mapearClientePastas(cid, c, set, organizar, forcarCriacao);
            });
        }
        log.info("Correlation: [{}[. Concluido mapeamento: de pastas de clientes", cid);
    }

    //listar todos os clientes ativos, inativos e suas pastas
    private synchronized Set<Path> listarPastasNaBase(String cid) throws EstruturaNotFoundException {
        verficarDiretoriosBaseECriar(cid);
        try {
            //listando todos os arquivos e corrigir nomes se necessarios
            Set<Path> actives = IOUtils.filtrarPorDiretorioERegex(base, regex.getInitById());
            log.info("Correlation: [{}]. Listando diretorios na pasta ativos. Listagem: {}", cid, actives.size());
            Set<Path> shutdowns = IOUtils.filtrarPorDiretorioERegex(shutdown, regex.getInitById());
            log.info("Correlation: [{}]. Listando diretorios na pasta inativos. Listagem: {}", cid, shutdowns.size());
            Set<Path> files = new HashSet<>();
            files.addAll(actives);
            files.addAll(shutdowns);
            log.info("Correlation: [{}]. Total de pastas: {}", cid, files.size());
            return files;
        } catch (IOException e) {
            log.error("Correlation: [{}]. Não foi possivel listas pastas dos clientes. ex=({})", cid, e.getMessage());
            throw new EstruturaNotFoundException("Nao foi possivel listar as pastas dos clientes", e.getCause());
        }
    }

    //mapeamento de pastas
    private void mapearClientePastas(String cid, Cliente c, Set<Path> files, boolean organizar, boolean forcarCreate) {
        Optional<Path> file = IOUtils.buscarPastaPorId(c, files);

        log.info("Correlation: [{}]. Buscando pasta para o cliente: {}, pasta encontrada=({})",
                cid, c.getIdFormatado(), file.isPresent()? file.get().toString() : null);

        Optional<ClienteDTO> opt = clientDTOList.stream().filter(f-> f.getApelido().equals(c.getId())).findFirst();
        //verificar se ja foi criado
        boolean isCriado = opt.map(ClienteDTO::isFolderCreate).orElse(true);
        log.info("Correlation: [{}]. Pasta do cliente: {} ja foi criada? {}", cid, c.getIdFormatado(), isCriado);

        Pair<Cliente, Path> pair;
        Path destinoDesligada = shutdown.resolve(c.toString());
        Path destinoAtiva = base.resolve(c.toString());

        if (file.isPresent()) {
            //verificar nome do arquivo se esta de acordo com a norma
            boolean nomeCorreto = file.get().getFileName().toString().equals(c.toString());
            log.info("Correlation: [{}]. Pasta do cliente: {} Encontrada:({}): Esta com o nome correto? {}",
                    cid, c.getIdFormatado(), file.get().toString(), nomeCorreto);

            //caminho do diretorio
            if(organizar) {
                if (c.getStatus().equalsIgnoreCase("Desligada"))
                    pair = moverPastaCliente(cid, c, file.get(), shutdown, nomeCorreto, destinoDesligada);
                else pair = moverPastaCliente(cid, c, file.get(), base, nomeCorreto, destinoAtiva);

                log.info("Correlation: [{}]. Pasta do cliente: {} foi renomeada de ({}) para ({})",
                        cid, c.getIdFormatado(), file.get().toString(), pair.getPath().toString());
            }
            else pair = new Pair<>(c,file.get());
        }

        //criar pasta apenas em condicao de que deva ser criado, principalmente em novos clientes
        else if (forcarCreate || !isCriado) {
            //criar pasta oficial caso não exista
            if (c.getStatus().equalsIgnoreCase("Desligada")) {
                pair = montarEstruturaNoCliente(cid, c,destinoDesligada);
            } else {
                pair = montarEstruturaNoCliente(cid, c, destinoAtiva);
            }
            log.info("Correlation: [{}]. Pasta do cliente: {}. Pasta criada {}",
                    cid, c.getIdFormatado(), pair.getPath().toString());

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
    private Pair<Cliente, Path> montarEstruturaNoCliente(String cid, Cliente c, Path path){
        Pair<Cliente, Path> pair = IOUtils.criarDiretorioCliente(c, path);
        log.info("Correlation: [{}]. Cliente:{}. Criado pasta: {}", cid, c.getIdFormatado(), path.toString());
        if(Files.exists(path)) {
            // usado para criar uma estrutura basica
            // de um novo cliente com pastas basicas como GERAL, FISCAL, CONTABIL
            List<ClientDefaultPathDTO> paths = clienteDAOService.listarPastasPadroes();
            for (ClientDefaultPathDTO pathDTO : paths) {
                try{
                    Path p = path.resolve(pathDTO.getValue());
                    IOUtils.criarDiretorios(p);
                }catch (IOException e){
                    log.error("Correlation: [{}]. Cliente: ({}). Falha ao criar estrutura basica {}, ex=({})",
                            cid, c.getIdFormatado(), pathDTO.getValue(), e.getMessage());
                }
            }
        }
        return pair;
    }

    private Pair<Cliente, Path> moverPastaCliente(String cid, Cliente c, Path file, Path base,
                                                  boolean nomeCorreto, Path destino){
        boolean localCorreto = file.getParent().equals(base);
        if (!localCorreto || !nomeCorreto) {
            log.info("Correlation: [{}]. Movendo cliente:{}. Criado pasta: {} de ({}) para ({})",
                    cid, c.getIdFormatado(), file.toString(), destino.toString());
            return ioService.mover(cid, c, file, destino);
        } else return new Pair<>(c, destino);
    }

    public Path buscarPastaDoClienteECriarSeNaoExistir(String cid, Cliente c) {
        iniciarlizarSeVazio(cid);
        Optional<Path> result = Optional.ofNullable(cliMap.get(c));
        if(result.isPresent()) return result.get();
        else {
            Path p = base.resolve(c.toString());
            Optional<Pair<Cliente,Path>> result2 = Optional.ofNullable(IOUtils.criarDiretorioCliente(c, p));
            if(result2.isPresent()){
                cliMap.put(c, result2.get().getPath());
                return result2.get().getPath();
            }
            else {
                log.error("Correlation: [{}]. Falha ao criar o diretorio {}", cid, p.toString());
                throw new EstruturaNotFoundException("Não foi possivel criar o diretorio: " + p.toString());
            }
        }
    }

    public Path buscarPastaBaseCliente(String cid, Cliente c) {
        iniciarlizarSeVazio(cid);
        return cliMap.get(c);
    }

    public Path buscarPastaBaseClientePorId(String cid, Long id) {
        log.info("Correlation: [{}]. Buscando pasta base do apelido {}", cid, id);
        Optional<Path> optional = buscarClienteEmMapPorId(cid, id)
                .map(c-> buscarPastaBaseCliente(cid, c));

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

    public Optional<Cliente> buscarClienteEmMapPorId(String cid, long id){
        iniciarlizarSeVazio(cid);

        Optional<Cliente> result = cliMap
                .keySet()
                .stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
        log.info("Correlation: [{}]. Encontrado cliente com o apelido {} ? {}", cid, id, result.isPresent());
        return result;
    }

    //verificar e criar estrutura de modelo
    public void verificarEstruturaNoModelo(Path estrutura) throws IOException {
        Path path = getModel().resolve(estrutura);
        IOUtils.criarDiretorios(path);
    }

    public void verficarDiretoriosBaseECriar(String cid) {
        log.info("Correlation: [{}]. Iniciando verificação de diretorios base", cid);

        base = Paths.get(serverFile.getBase());
        shutdown = Paths.get(serverFile.getShutdown());
        model = Paths.get(serverFile.getModel());
        try {
            if (!IOUtils.verificarSeExiste(base)) {
                log.error("Correlation: [{}]. Pasta {} não existe", cid, base.toString());
                throw new EstruturaNotFoundException("Base de arquivos não existe");
            }
            IOUtils.criarDiretorio(shutdown);
            IOUtils.criarDiretorio(model);
            log.info("Correlation: [{}]. Concluindo verificação de pastas base", cid);
        } catch (IOException e) {
            log.error("Correlation: [{}]. Falha ao criar pastas ex=({})", cid, e.getMessage());
            throw new EstruturaNotFoundException("Pastas importantes não foram encontradas: "
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
