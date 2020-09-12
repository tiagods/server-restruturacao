package com.tiagods.prolink.service;

import com.tiagods.prolink.config.Regex;
import com.tiagods.prolink.io.IOService;
import com.tiagods.prolink.model.Cliente;
import com.tiagods.prolink.model.TipoArquivo;
import com.tiagods.prolink.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ProcessarService {

    @Autowired private Regex regex;
    @Autowired private ClienteService clienteService;
    @Autowired private IOService ioService;

    public void moverPastaOuArquivo(String cid, Path pastaBaseScannear, Path estrutura, String apelido,
                                    boolean travarEstrutura, TipoArquivo tipoArquivo) {
        if(tipoArquivo.equals(TipoArquivo.PASTA)) {
            log.info("Correlation: [{}]. Processamento de arquivos por tipo: PASTA", cid);
            moverPasta(cid, apelido, pastaBaseScannear, estrutura, travarEstrutura);
        } else if(tipoArquivo.equals(TipoArquivo.ARQUIVO)) {
            log.info("Correlation: [{}]. Processamento de arquivos por tipo: ARQUIVO", cid);
            moverArquivo(cid, apelido, pastaBaseScannear, estrutura, travarEstrutura);
        }
    }

    public void moverArquivo(String cid, String apelido, Path pastaBaseScannear, Path estrutura, boolean travarEstrutura) {
        log.info("Correlation: [{}]. Iniciando movimentação de arquivos: [{}]", cid, pastaBaseScannear);
        try {
            clienteService.verificarEstruturaNoModelo(estrutura);
            processarArquivos(cid, null, false, null, apelido, Files.list(pastaBaseScannear).iterator(),
                    estrutura, travarEstrutura);
        } catch (IOException e) {
            log.error("Correlation: [{}]. Falha ao abrir pasta ({}).", cid, pastaBaseScannear);
        }
    }

    public void moverPasta(String cid, String apelido, Path pastaBaseScannear, Path estrutura, boolean travarEstrutura) {
        try {
            log.info("Correlation: [{}]. Iniciando movimentação de arquivos: [{}]", cid, pastaBaseScannear);
            clienteService.verificarEstruturaNoModelo(estrutura);
            Optional<String> optionalS = Optional.ofNullable(apelido);
            String newRegex = "";
            if(optionalS.isPresent()) {
                newRegex = apelido;
                //newRegex = regex.getInitByIdReplaceNickName().replace("nickName", nickName);
            } else {
                newRegex = regex.getInitById();
            }

            Map<Path,String> mapClientes = IOUtils.listByDirectoryDefaultToMap(pastaBaseScannear, newRegex);
            log.info("Correlation: [{}]. Clientes encontrados com o regex: [{}}]. Tamanho: [{}]", cid, newRegex, mapClientes.size());
            Map<Path, Cliente> mapPath = new HashMap<>();
            mapClientes.keySet().forEach(c->{
                Long l = Long.parseLong(mapClientes.get(c));
                clienteService.buscarClienteEmMapPorId(cid, l)
                        .ifPresent(r->mapPath.put(c,r));
            });
            //vai mover apenas os arquivos de dentro das pastas, as pastas irao continuar
            int i = 1;
            int total = mapPath.keySet().size();
            for(Path p : mapPath.keySet()) {
                if(clienteService.containsFolderToJob(p)) {
                    continue;
                } else {
                    Cliente cli = mapPath.get(p);
                    clienteService.addFolderToJob(p);
                    log.info("Correlation: [{}]. Estrutura: ({}). Processando item=[{} de {}] do cliente: {}",
                            cid, estrutura.toString(), i, total, cli.getIdFormatado());
                    Path pastaDoCliente = clienteService.buscarPastaDoClienteECriarSeNaoExistir(cid, cli);
                    if (pastaDoCliente != null) {
                        try {
                            processarArquivos(cid, cli, true, pastaDoCliente, apelido, Files.list(p).iterator(),
                                    estrutura, travarEstrutura);
                        } catch (IOException e) {
                            log.error("Correlation: [{}]. Falha ao abrir pasta ({}).", cid, (p.toString()));
                        }
                    }
                    clienteService.removeFolderToJob(p);
                }
                i++;
            }
        }catch (IOException e){
            log.error("Correlation: [{}]. Movimentação cancelada por erro: {}", cid, e.getMessage());
        }
    }

    // travar estrutura = evitar criação de subspastas e usar diretorio fixo C:/CLIENTE/OBRIGAGACAO/ANO/MES
    //inicia processo, vai percorrer todas as pastar e ira mover conteudo para um novo diretorio
    private void processarArquivos(String cid, Cliente cli, boolean renomearSemId, Path pastaCliente,
                                   String apelido, Iterator<Path> files, Path estrutura, boolean travarEstrutura){
        while (files.hasNext()) {
            Path file  = files.next();
            if(Files.isDirectory(file)) {
                try {
                    //impedir criacao de sucessivas subpastas
                    Path estruturaFinal = travarEstrutura ? estrutura : estrutura.resolve(file.getFileName());
                    processarArquivos(cid, cli, renomearSemId, pastaCliente, apelido, Files.list(file).iterator(), estruturaFinal, travarEstrutura);
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
            else {
                //processar quando não for enviado cliente, entende-se que é para descobrir o cliente de cada arquivo
                if(cli == null && pastaCliente == null) {
                    String fileName = file.getFileName().toString();

                    if(apelido!=null){//mover arquivo pelo apelido
                        Matcher matcher = Pattern.compile(regex.getInitByIdReplaceNickName()
                                .replace("nickName", apelido)).matcher(fileName);
                        if(matcher.find()){
                            Optional<Cliente> cliente = clienteService.buscarClienteEmMapPorId(cid, Long.parseLong(apelido));
                            cliente.ifPresent(c-> {
                                Path p = clienteService.buscarPastaBaseCliente(cid, c);
                                ioService.mover(cid, c, false, file, p, estrutura);
                            });
                        }
                    }
                    else {
                        Matcher matcher = Pattern.compile(regex.getInitById()).matcher(fileName);
                        Matcher matcher2 = Pattern.compile(regex.getExtractId()).matcher(fileName);
                        if (matcher.find() && matcher2.find()) {
                            Optional<Cliente> cliente = clienteService.buscarClienteEmMapPorId(cid, Long.parseLong(matcher2.group()));
                            cliente.ifPresent(c -> {
                                Path p = clienteService.buscarPastaBaseCliente(cid, c);
                                ioService.mover(cid, c, false, file, p, estrutura);
                            });
                        }
                    }
                } else {
                    //base - subpastas - arquivo
                    ioService.mover(cid, cli, renomearSemId, file, pastaCliente, estrutura);
                }
            }
        }
    }
}
