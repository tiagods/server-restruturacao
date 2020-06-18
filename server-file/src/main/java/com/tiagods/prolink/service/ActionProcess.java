package com.tiagods.prolink.service;

import com.tiagods.prolink.config.Regex;
import com.tiagods.prolink.exception.FolderCuncurrencyJob;
import com.tiagods.prolink.exception.ParametroIncorretoException;
import com.tiagods.prolink.exception.ParametroNotFoundException;
import com.tiagods.prolink.exception.PathInvalidException;
import com.tiagods.prolink.model.Cliente;
import com.tiagods.prolink.model.Obrigacao;
import com.tiagods.prolink.model.PathJob;
import com.tiagods.prolink.obrigacao.ObrigacaoContrato;
import com.tiagods.prolink.obrigacao.ObrigacaoFactory;
import com.tiagods.prolink.obrigacao.Periodo;
import com.tiagods.prolink.utils.IOUtils;
import com.tiagods.prolink.utils.MyStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Month;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ActionProcess {
    @Autowired private Regex regex;
    @Autowired private ClienteService clientIOService;
    @Autowired private IOService ioService;
    //@Autowired
    //private ArquivoService arquivoService;

    private Logger log = LoggerFactory.getLogger(getClass());

    //mover por pastas
    @Async
    public void iniciarMovimentacaoPorPasta(PathJob pathJob, String nickName) throws FolderCuncurrencyJob {
        //Path path = Paths.get("c:/job");
        //Path novaEstrutura = Paths.get("GERAL","SAC");
        Path job = Paths.get(pathJob.getDirForJob());
        Path estrutura = Paths.get(pathJob.getEstrutura());
        clientIOService.verficarDiretoriosBaseECriar();
        moverPasta(job, estrutura, nickName, false);
        log.info("Concluido movimentação em=["+job.toString()+"]");
    }

    public void moverObrigacao(ObrigacaoContrato contrato, Obrigacao obrigacao) {
        clientIOService.verficarDiretoriosBaseECriar();
        Obrigacao.Tipo tipo = obrigacao.getTipo();
        Path job = Paths.get(obrigacao.getDirForJob());
        Month mesJob = obrigacao.getMes();
        Year anoJob = obrigacao.getAno();

        if(contrato.contains(Periodo.ANO)) {
            String pastaAnoObrigatoria = anoJob == null ? null : contrato.getPastaNome(Periodo.ANO, anoJob, mesJob);
            String pastaMesObrigatoria = mesJob == null ? null : contrato.getPastaNome(Periodo.MES, anoJob, mesJob);
            String clienteApelido = obrigacao.getCliente()!=null ? MyStringUtils.novoApelido(obrigacao.getCliente()) : null;
            log.info("Pasta do ano informada? = [" + pastaAnoObrigatoria + "]");
            log.info("Pasta do mes informada? = [" + pastaMesObrigatoria + "]");

            capturarPastasPeriodo(job, Periodo.ANO, pastaAnoObrigatoria)
                    .forEach(folderAno -> {
                        log.info("Listando pasta: ");
                        if (contrato.contains(Periodo.MES)) {
                            String nomePasta = folderAno.getFileName().toString();
                            log.info("Nome da pasta ano=[" + nomePasta + "]=Origem=[" + folderAno.toString() + "]");
                            //pegar o nome do diretorio e remover qualquer outro adicional do nome para pegar o periodo mes ou ano
                            try {
                                String ano = contrato.getMesOuAno(Periodo.ANO, nomePasta);
                                log.info("Ano da pasta=[" + ano + "]");
                                Path estrutura = Paths.get(tipo.getEstrutura(), ano);
                                log.info("Nome da estrutura=[" + estrutura + "]");

                                capturarPastasPeriodo(folderAno, Periodo.MES, pastaMesObrigatoria)
                                        .forEach(folderMes -> {
                                            String mes = folderMes.getFileName().toString();
                                            try {
                                                mes = contrato.getMesOuAno(Periodo.MES, mes);
                                                log.info("Nome da pasta mes: " + mes + "\t" + folderMes.toString());
                                                log.info("Mes da pasta=[" + mes + "]");
                                                Path novaEstrutura = estrutura.resolve(mes);
                                                log.info("Estrutura pasta mes=[" + novaEstrutura + "]");
                                                Files.list(folderMes).forEach(c->{
                                                    moverPasta(c, novaEstrutura, clienteApelido, true);
                                                });
                                            } catch (ParametroNotFoundException e) {
                                                log.error(e.getMessage());
                                            } catch (ParametroIncorretoException | IOException e) {
                                                log.error(e.getMessage());
                                            }
                                        });
                            } catch (ParametroNotFoundException e) {
                                log.error(e.getMessage());
                            } catch (ParametroIncorretoException e) {
                                log.error(e.getMessage());
                            }
                        }
                    });
        }
        log.info("Concluido movimentação em=["+job.toString()+"]");
    }

    Set<Path> capturarPastasPeriodo(Path job, Periodo periodo, String nomeObrigatorioPasta) {
        log.info("Pasta do periodo "+periodo+" informada? = ["+nomeObrigatorioPasta+"]");
        Set<Path> files = new LinkedHashSet<>();
        try {
            files = Files.list(job)
                    .filter(filter ->
                            nomeObrigatorioPasta==null ? Files.isDirectory(filter)
                                    : Files.isDirectory(filter) && filter.getFileName().toString().equals(nomeObrigatorioPasta)
                    )
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            log.error("Erro ao carregar listagem da pasta=["+job+"]");
        }
        return files;
    }

    public void moverPasta(Path job, Path estrutura, String nickName, boolean travarEstrutura) {
        try {
            log.info("Iniciando movimentação de arquivos");

            clientIOService.verifyStructureInModel(estrutura);
            Optional<String> optionalS = Optional.ofNullable(nickName);
            String newRegex = "";
            if(optionalS.isPresent()) {
                newRegex = regex.getInitByIdReplaceNickName().replace("nickName", nickName);
            } else {
                newRegex = regex.getInitById();
            }

            Map<Path,String> mapClientes = IOUtils.listByDirectoryDefaultToMap(job, newRegex);
            log.info("Clientes encontrados com o regex: "+newRegex+" = "+mapClientes.size());

            Map<Path,Cliente> mapPath = new HashMap<>();
            mapClientes.keySet().forEach(c->{
                Long l = Long.parseLong(mapClientes.get(c));
                clientIOService.findMapClientById(l).ifPresent(r->mapPath.put(c,r));
            });
            //vai mover apenas os arquivos de dentro das pastas, as pastas irao continuar
            int i = 1;
            int total = mapPath.keySet().size();
            for(Path p : mapPath.keySet()) {
                if(clientIOService.containsFolderToJob(p)) {
                    continue;
                } else {
                    Cliente cli = mapPath.get(p);
                    clientIOService.addFolderToJob(p);
                    log.info(estrutura.toString() + " - Processando Item=["+i+"]de["+total+"] Cliente=[" + cli.getIdFormatado()+"]");
                    Path basePath = clientIOService.searchClientPathBaseAndCreateIfNotExists(cli);
                    if (basePath != null) {
                        try {
                            processarPorPasta(cli, true, basePath, Files.list(p).iterator(), estrutura, travarEstrutura);
                        } catch (IOException e) {
                            log.error("Falha ao abrir pasta ".concat(p.toString()));
                        }
                    }
                    clientIOService.removeFolderToJob(p);
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
