package com.tiagods.prolink.service;

import com.tiagods.prolink.exception.FolderCuncurrencyJob;
import com.tiagods.prolink.exception.ParametroIncorretoException;
import com.tiagods.prolink.exception.ParametroNotFoundException;
import com.tiagods.prolink.exception.PathInvalidException;
import com.tiagods.prolink.model.Obrigacao;
import com.tiagods.prolink.model.PathJob;
import com.tiagods.prolink.obrigacao.ObrigacaoContrato;
import com.tiagods.prolink.obrigacao.ObrigacaoFactory;
import com.tiagods.prolink.obrigacao.Periodo;
import com.tiagods.prolink.utils.MyStringUtils;
import lombok.extern.slf4j.Slf4j;
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

    @Autowired private ClienteService clienteService;
    @Autowired private OperacaoService operacaoService;

    @Async
    public void iniciarMovimentacaoPorPasta(String cid, PathJob pathJob, String nickName) {
        Path job = Paths.get(pathJob.getDirForJob());
        Path estrutura = Paths.get(pathJob.getEstrutura());
        clienteService.verficarDiretoriosBaseECriar(cid);
        operacaoService.moverPasta(cid, job, estrutura, nickName, false);
        log.info("Correlation [{}]. Concluindo movimentacao {}", cid, job.toString());
    }

    @Async
    public void iniciarMovimentacaoPorObrigacao(String cid, ObrigacaoContrato contrato, Obrigacao obrigacao) throws ParametroNotFoundException, PathInvalidException {
        clienteService.verficarDiretoriosBaseECriar(cid);
        Obrigacao.Tipo tipo = obrigacao.getTipo();
        Path job = Paths.get(obrigacao.getDirForJob());
        Month mesJob = obrigacao.getMes();
        Year anoJob = obrigacao.getAno();

        if(contrato.contains(Periodo.ANO)) {
            String pastaAnoObrigatoria = anoJob == null ? null : contrato.getPastaNome(Periodo.ANO, anoJob, mesJob);
            String pastaMesObrigatoria = mesJob == null ? null : contrato.getPastaNome(Periodo.MES, anoJob, mesJob);
            String clienteApelido = obrigacao.getCliente()!=null ? MyStringUtils.novoApelido(obrigacao.getCliente()) : null;
            log.info("Correlation: [{}]. Pasta ano informada? ", cid, pastaAnoObrigatoria);
            log.info("Correlation: [{}]. Pasta mes informada? ", cid, pastaMesObrigatoria);

            capturarPastasPeriodo(cid, job, Periodo.ANO, pastaAnoObrigatoria)
                    .forEach(folderAno -> {
                        String nomePasta = folderAno.getFileName().toString();
                        log.info("Nome da pasta ano=[" + nomePasta + "]=Origem=[" + folderAno.toString() + "]");
                        //pegar o nome do diretorio e remover qualquer outro adicional do nome para pegar o periodo mes ou ano
                        try {
                            String ano = contrato.getMesOuAno(Periodo.ANO, nomePasta);
                            log.info("Ano da pasta=[" + ano + "]");
                            Path estrutura = Paths.get(tipo.getEstrutura(), ano);
                            log.info("Nome da estrutura=[" + estrutura + "]");

                            if (contrato.contains(Periodo.MES)) {
                                capturarPastasPeriodo(cid, folderAno, Periodo.MES, pastaMesObrigatoria)
                                        .forEach(folderMes -> {
                                            String mes = folderMes.getFileName().toString();
                                            try {
                                                mes = contrato.getMesOuAno(Periodo.MES, mes);
                                                log.info("Nome da pasta mes: " + mes + "\t" + folderMes.toString());
                                                log.info("Mes da pasta=[" + mes + "]");
                                                Path novaEstrutura = estrutura.resolve(mes);
                                                log.info("Estrutura pasta mes=[" + novaEstrutura + "]");
                                                operacaoService.moverPasta(cid, folderMes, novaEstrutura, clienteApelido, true);
                                            } catch (ParametroNotFoundException e) {
                                                log.error(e.getMessage());
                                            } catch (ParametroIncorretoException e) {
                                                log.error(e.getMessage());
                                            }
                                        });
                            } else {
                                //se no contrato não houver MES, ira mover sob um nivel acima ANO
                                operacaoService.moverPasta(cid, folderAno, estrutura, clienteApelido, true);
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

    public ObrigacaoContrato validarObrigacao(String cid, Obrigacao obrigacao) throws ParametroNotFoundException, PathInvalidException, ParametroIncorretoException {
        log.info("Correlation: [{}]. Pushed: ({})",cid, obrigacao);
        ObrigacaoContrato ob = ObrigacaoFactory.get(obrigacao);
        Obrigacao.Tipo tipo = obrigacao.getTipo();
        Path dirForJob = Paths.get(obrigacao.getDirForJob());
        if(!dirForJob.getFileName().toString().equals(tipo.getDescricao())) {
            String message = "O diretorio informado é invalido para essa obrigação";
            log.error(message);
            throw new PathInvalidException(message);
        }
        if(obrigacao.getAno()==null && ob.contains(Periodo.ANO)) {
            String message = "O parametro ano é obrigatório para essa obrigação";
            log.error(message);
            throw new ParametroNotFoundException(message);
        }
        if(obrigacao.getMes()!=null && !ob.contains(Periodo.MES)) {
            log.error("Mes informado para uma obrigação anual");
            throw new ParametroIncorretoException("Obrigação "+tipo.getDescricao()+" é anual e o mês não deve ser informado");
        }
//        if(obrigacao.getMes()==null && ob.contains(Periodo.MES)) {
//            log.info("Mes não informado para uma obrigação mensal");
//            throw new ParametroNotFoundException("O parametro mês é obrigatório para essa obrigação");
//        }
        return ob;
    }

    Set<Path> capturarPastasPeriodo(String cid, Path job, Periodo periodo, String nomeObrigatorioPasta) {
        log.info("Correlation: [{}].  Pasta do periodo informado:{} . Nome de pasta: ({})", cid, periodo, nomeObrigatorioPasta);
        Set<Path> files = new LinkedHashSet<>();
        try {
            files = Files.list(job)
                    .filter(filter ->
                            nomeObrigatorioPasta==null ? Files.isDirectory(filter)
                                    : Files.isDirectory(filter) && filter.getFileName().toString().equals(nomeObrigatorioPasta)
                    )
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            log.error("Correlation: [{}]. Erro ao carregar listagem da pasta={}", cid, job.toString());
        }
        return files;
    }


}
