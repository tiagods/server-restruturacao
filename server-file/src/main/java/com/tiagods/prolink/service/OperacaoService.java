package com.tiagods.prolink.service;

import com.tiagods.prolink.config.Regex;
import com.tiagods.prolink.io.IOService;
import com.tiagods.prolink.model.Cliente;
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

@Slf4j
@Service
public class OperacaoService {

    @Autowired private Regex regex;
    @Autowired private ClienteService clienteService;
    @Autowired private IOService ioService;

    public void moverPasta(String cid, Path pastaBaseScannear, Path estrutura, String nickName, boolean travarEstrutura) {
        try {
            log.info("Iniciando movimentação de arquivos=[" +pastaBaseScannear+"]");
            log.info("Apelido selecionado=[" +pastaBaseScannear+"]");
            clienteService.verificarEstruturaNoModelo(estrutura);
            Optional<String> optionalS = Optional.ofNullable(nickName);
            String newRegex = "";
            if(optionalS.isPresent()) {
                newRegex = nickName;
                //newRegex = regex.getInitByIdReplaceNickName().replace("nickName", nickName);
            } else {
                newRegex = regex.getInitById();
            }

            Map<Path,String> mapClientes = IOUtils.listByDirectoryDefaultToMap(pastaBaseScannear, newRegex);
            log.info("Clientes encontrados com o regex: ["+newRegex+"] = ["+mapClientes.size()+"]");

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
                    log.info(estrutura.toString() + " - Processando Item=["+i+"]de["+total+"] Cliente=[" + cli.getIdFormatado()+"]");
                    Path basePath = clienteService.buscarPastaDoClienteECriarSeNaoExistir(cid, cli);
                    if (basePath != null) {
                        try {
                            processarPorPasta(cli, true, basePath, Files.list(p).iterator(), estrutura, travarEstrutura);
                        } catch (IOException e) {
                            log.error("Falha ao abrir pasta ".concat(p.toString()));
                        }
                    }
                    clienteService.removeFolderToJob(p);
                }
                i++;
            }
        }catch (IOException e){
            log.error(e.getMessage());
            log.info("Movimentação cancelada por erro");
        }
    }

    // travar estrutura = evitar criação de subspastas e usar diretorio fixo C:/CLIENTE/OBRIGAGACAO/ANO/MES
    //inicia processo, vai percorrer todas as pastar e ira mover conteudo para um novo diretorio
    private void processarPorPasta(Cliente cli, boolean renomearSemId, Path basePath, Iterator<Path> files, Path estrutura, boolean travarEstrutura){
        while (files.hasNext()) {
            Path file  = files.next();
            if(Files.isDirectory(file)) {
                try {
                    //impedir criacao de sucessivas subpastas
                    Path estruturaFinal = travarEstrutura ? estrutura : estrutura.resolve(file.getFileName());
                    processarPorPasta(cli, renomearSemId, basePath, Files.list(file).iterator(), estruturaFinal, travarEstrutura);
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
            else {
                //base - subpastas - arquivo
                Path estruturaFinal = basePath.resolve(estrutura);
                ioService.mover(cli, renomearSemId, file, basePath, estruturaFinal);
            }
        }
    }
}
