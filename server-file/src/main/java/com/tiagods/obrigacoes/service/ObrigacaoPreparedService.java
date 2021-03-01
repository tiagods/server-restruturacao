package com.tiagods.obrigacoes.service;

import com.tiagods.obrigacoes.exception.ParametroIncorretoException;
import com.tiagods.obrigacoes.exception.ParametroNotFoundException;
import com.tiagods.obrigacoes.exception.PathInvalidException;
import com.tiagods.obrigacoes.model.Obrigacao;
import com.tiagods.obrigacoes.model.PathJob;
import com.tiagods.obrigacoes.obrigacao.ObrigacaoContrato;
import com.tiagods.obrigacoes.obrigacao.ObrigacaoFactory;
import com.tiagods.obrigacoes.obrigacao.Periodo;
import com.tiagods.obrigacoes.utils.MyStringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ObrigacaoPreparedService {

    @Autowired private ClienteService clienteService;
    @Autowired private ProcessarService operacaoService;

    public void iniciarMovimentacaoPorObrigacaoGeral(String cid, Obrigacao.Tipo tipo, String dir, Set<LocalDate> periodos) {
        cid = (cid != null) ? cid : UUID.randomUUID().toString();
        log.info("Correlation: [{}]. Movendo por obrigacao = {}, pasta=({}), periodos=({})", cid, tipo, dir, periodos);

        Path diretorio = Paths.get(dir);
        boolean existe = Files.exists(diretorio);
        boolean contemArquivos = false;

        try {
            contemArquivos = Files.isDirectory(diretorio)
                    && StringUtils.hasText(dir)
                    && Files.list(diretorio).count() > 0;
        } catch(IOException ex) {
            log.error("Correlation: [{}]. Nao foi possivel listar a pasta {}", cid, dir);
        }

        if(tipo != null && existe && contemArquivos) {
            for (LocalDate localDate : periodos) {
                Obrigacao obrigacao = new Obrigacao();
                obrigacao.setMes(localDate.getMonth());
                obrigacao.setAno(Year.of(localDate.getYear()));
                obrigacao.setTipo(tipo);
                obrigacao.setDirForJob(dir);
                ObrigacaoContrato contrato = ObrigacaoFactory.get(obrigacao);
                if(contrato.contains(Periodo.MES)) {
                    try {
                        iniciarMovimentacaoPorObrigacao(cid, contrato, obrigacao);
                    } catch (ParametroNotFoundException e) {
                        e.printStackTrace();
                    } catch (PathInvalidException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Async
    public void iniciarMovimentacaoPorPasta(String cid, PathJob pathJob, String nickName) {
        Path job = Paths.get(pathJob.getDirForJob());
        Path estrutura = Paths.get(pathJob.getEstrutura());
        clienteService.verificarDiretoriosBaseECriar(cid);
        operacaoService.moverPasta(cid, nickName,  job, estrutura, false);
        log.info("Correlation: [{}]. Concluindo movimentacao {}", cid, job.toString());
    }

    public void iniciarMovimentacaoPorObrigacao(String cid, ObrigacaoContrato contrato, Obrigacao obrigacao) throws ParametroNotFoundException, PathInvalidException {
        clienteService.verificarDiretoriosBaseECriar(cid);
        Obrigacao.Tipo tipo = obrigacao.getTipo();
        Path job = Paths.get(obrigacao.getDirForJob());
        Month mesJob = obrigacao.getMes();
        Year anoJob = obrigacao.getAno();

        if(contrato.contains(Periodo.ANO)) {
            String pastaAnoObrigatoria = anoJob == null ? null : contrato.getPastaNome(Periodo.ANO, anoJob, mesJob);
            String pastaMesObrigatoria = mesJob == null ? null : contrato.getPastaNome(Periodo.MES, anoJob, mesJob);
            String clienteApelido = obrigacao.getCliente()!=null ? MyStringUtils.novoApelido(obrigacao.getCliente()) : null;
            log.info("Correlation: [{}]. Pasta ano informada? {}", cid, pastaAnoObrigatoria);
            log.info("Correlation: [{}]. Pasta mes informada? {}", cid, pastaMesObrigatoria);

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
                                                operacaoService.moverPastaOuArquivo(cid, folderMes, novaEstrutura, clienteApelido, true, tipo.getTipoArquivo());
                                            } catch (ParametroNotFoundException e) {
                                                log.error(e.getMessage());
                                            } catch (ParametroIncorretoException e) {
                                                log.error(e.getMessage());
                                            }
                                        });
                            } else {
                                //se no contrato não houver MES, ira mover sob um nivel acima ANO
                                operacaoService.moverPastaOuArquivo(cid, folderAno, estrutura, clienteApelido, true, tipo.getTipoArquivo());
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
        log.info("Correlation: [{}]. Iniciando Validacao: ({})", cid, obrigacao);
        ObrigacaoContrato ob = ObrigacaoFactory.get(obrigacao);
        Obrigacao.Tipo tipo = obrigacao.getTipo();
        Path dirForJob = Paths.get(obrigacao.getDirForJob());
        if(!dirForJob.getFileName().toString().equals(tipo.getDescricao())) {
            log.error("Correlation: [{}]. O diretorio informado é invalido para essa obrigação. Dir: ({})", cid, dirForJob);
            throw new PathInvalidException("O diretorio informado é invalido para essa obrigação");
        }
        if(obrigacao.getAno()==null && ob.contains(Periodo.ANO)) {
            log.error("Correlation: [{}]. O parametro ano é obrigatorio para essa obrigacao", cid);
            throw new ParametroNotFoundException("O parametro ano é obrigatório para essa obrigação");
        }
        if(obrigacao.getMes()!=null && !ob.contains(Periodo.MES)) {
            log.error("Correlation: [{}]. Mes informado para uma obrigação anual", cid);
            throw new ParametroIncorretoException("Obrigação "+tipo.getDescricao()+" é anual e o mês não deve ser informado");
        }
//        if(obrigacao.getMes()==null && ob.contains(Periodo.MES)) {
//            log.info("Mes não informado para uma obrigação mensal");
//            throw new ParametroNotFoundException("O parametro mês é obrigatório para essa obrigação");
//        }
        return ob;
    }

    Set<Path> capturarPastasPeriodo(String cid, Path job, Periodo periodo, String nomeObrigatorioPasta) {
        log.info("Correlation: [{}].  Pasta do periodo informado: ({}) . Nome de pasta: ({})", cid, periodo, nomeObrigatorioPasta);
        Set<Path> files = new LinkedHashSet<>();
        try {
            files = Files.list(job)
                    .filter(filter ->
                            nomeObrigatorioPasta==null ? Files.isDirectory(filter)
                                    : Files.isDirectory(filter) && filter.getFileName().toString().equals(nomeObrigatorioPasta)
                    )
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            log.error("Correlation: [{}]. Erro ao carregar listagem da pasta: ({})", cid, job.toString());
        }
        return files;
    }
}
