package com.tiagods.obrigacoes.service;

import com.tiagods.obrigacoes.config.Regex;
import com.tiagods.obrigacoes.service.io.IOService;
import com.tiagods.obrigacoes.model.Cliente;
import com.tiagods.obrigacoes.model.TipoArquivo;
import com.tiagods.obrigacoes.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

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
            processarArquivos(cid, null, false, null, apelido, pastaBaseScannear,
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
                        processarArquivos(cid, cli, true, pastaDoCliente, apelido, p,
                                estrutura, travarEstrutura);
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
                                   String apelido, Path path, Path estrutura, boolean travarEstrutura) throws IOException{
        List<Path> files = Files.list(path).collect(toList());
        files.forEach(file ->{
            if(Files.isDirectory(file)) {
                try {
                    //impedir criacao de sucessivas subpastas
                    Path estruturaFinal = travarEstrutura ? estrutura : estrutura.resolve(file.getFileName());
                    processarArquivos(cid, cli, renomearSemId, pastaCliente, apelido, file, estruturaFinal, travarEstrutura);
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            } else {
                Path fileResult = file;
                String fileName = fileResult.getFileName().toString();
                Matcher matcher = Pattern.compile(regex.getInitByCnpj()).matcher(fileName);
                Matcher matcher2 = Pattern.compile(regex.getExtractCnpj()).matcher(fileName);

                //verificando se o arquivo contem um cnpj
                if(matcher.find() && matcher2.find()) {
                    Optional<Cliente> cliente = clienteService.buscarClienteEmMapPorCnpj(cid, matcher2.group());
                    cliente.ifPresent(c-> {
                        if(apelido == null || c.getIdFormatado().equals(apelido)) {
                            Path p = clienteService.buscarPastaBaseCliente(cid, c);
                            ioService.mover(cid, c, true, fileResult, p, estrutura);
                        }
                    });
                }
                //processar quando não for enviado cliente, entende-se que é para descobrir o cliente de cada arquivo
                else if (cli == null && pastaCliente == null) {
                    if(apelido!=null) {//mover arquivo pelo apelido
                        Matcher matcher3 = Pattern.compile(regex.getInitByIdReplaceNickName()
                                .replace("nickName", apelido)).matcher(fileName);
                        if(matcher3.find()){
                            Optional<Cliente> cliente = clienteService.buscarClienteEmMapPorId(cid, Long.parseLong(apelido));
                            cliente.ifPresent(c-> {
                                Path p = clienteService.buscarPastaBaseCliente(cid, c);
                                ioService.mover(cid, c, false, fileResult, p, estrutura);
                            });
                        }
                    } else {
                        Matcher matcher3 = Pattern.compile(regex.getInitById()).matcher(fileName);
                        Matcher matcher4 = Pattern.compile(regex.getExtractId()).matcher(fileName);
                        if (matcher3.find() && matcher4.find()) {
                            Optional<Cliente> cliente = clienteService.buscarClienteEmMapPorId(cid, Long.parseLong(matcher4.group()));
                            cliente.ifPresent(c -> {
                                Path p = clienteService.buscarPastaBaseCliente(cid, c);
                                ioService.mover(cid, c, false, fileResult, p, estrutura);
                            });
                        }
                    }
                } else {
                    //base - subpastas - arquivo
                    ioService.mover(cid, cli, renomearSemId, fileResult, pastaCliente, estrutura);
                }
            }
        });
    }
}
