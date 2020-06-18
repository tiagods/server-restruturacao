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
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
public class ObrigacaoPreparedService {

    @Autowired private ClienteService clientIOService;
//    @Autowired private IOService ioService;
    @Autowired private OperacaoService operacaoService;


    //mover por pastas
    @Async
    public void iniciarMovimentacaoPorPasta(PathJob pathJob, String nickName) throws FolderCuncurrencyJob {
        //Path path = Paths.get("c:/job");
        //Path novaEstrutura = Paths.get("GERAL","SAC");
        Path job = Paths.get(pathJob.getDirForJob());
        Path estrutura = Paths.get(pathJob.getEstrutura());
        clientIOService.verficarDiretoriosBaseECriar();
        operacaoService.moverPasta(job, estrutura, nickName, false);
        log.info("Concluido movimentação em=["+job.toString()+"]");
    }

    public void iniciarMovimentacaoPorObrigacao(ObrigacaoContrato contrato, Obrigacao obrigacao) {
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

                            String nomePasta = folderAno.getFileName().toString();
                            log.info("Nome da pasta ano=[" + nomePasta + "]=Origem=[" + folderAno.toString() + "]");
                            //pegar o nome do diretorio e remover qualquer outro adicional do nome para pegar o periodo mes ou ano
                            try {
                                String ano = contrato.getMesOuAno(Periodo.ANO, nomePasta);
                                log.info("Ano da pasta=[" + ano + "]");
                                Path estrutura = Paths.get(tipo.getEstrutura(), ano);
                                log.info("Nome da estrutura=[" + estrutura + "]");

                                if (contrato.contains(Periodo.MES)) {
                                    capturarPastasPeriodo(folderAno, Periodo.MES, pastaMesObrigatoria)
                                            .forEach(folderMes -> {
                                                String mes = folderMes.getFileName().toString();
                                                try {
                                                    mes = contrato.getMesOuAno(Periodo.MES, mes);
                                                    log.info("Nome da pasta mes: " + mes + "\t" + folderMes.toString());
                                                    log.info("Mes da pasta=[" + mes + "]");
                                                    Path novaEstrutura = estrutura.resolve(mes);
                                                    log.info("Estrutura pasta mes=[" + novaEstrutura + "]");
                                                    Files.list(folderMes).forEach(c -> {
                                                        operacaoService.moverPasta(c, novaEstrutura, clienteApelido, true);
                                                    });
                                                } catch (ParametroNotFoundException e) {
                                                    log.error(e.getMessage());
                                                } catch (ParametroIncorretoException | IOException e) {
                                                    log.error(e.getMessage());
                                                }
                                            });
                                } else {
                                    //se no contrato não houver MES, ira mover sob um nivel acima ANO
                                    operacaoService.moverPasta(folderAno, estrutura, clienteApelido, true);
                                }
                            } catch (ParametroNotFoundException e) {
                                log.error(e.getMessage());
                            } catch (ParametroIncorretoException e) {
                                log.error(e.getMessage());
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


}
