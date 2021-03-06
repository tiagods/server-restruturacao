package com.tiagods.obrigacoes;

import com.tiagods.obrigacoes.exception.ParametroIncorretoException;
import com.tiagods.obrigacoes.exception.ParametroNotFoundException;
import com.tiagods.obrigacoes.exception.PathInvalidException;
import com.tiagods.obrigacoes.model.Obrigacao;
import com.tiagods.obrigacoes.obrigacao.ObrigacaoContrato;
import com.tiagods.obrigacoes.obrigacao.ObrigacaoFactory;
import com.tiagods.obrigacoes.obrigacao.Periodo;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Month;
import java.time.Year;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
@Slf4j
public class UtilsValidatorTest {
    public static void main(String[] args) throws Exception {
        Obrigacao obrigacao = new Obrigacao();
        obrigacao.setTipo(Obrigacao.Tipo.PROLINKDIGITAL);
        obrigacao.setDirForJob("c:\\Temp\\PROLINK DIGITAL");
        obrigacao.setAno(Year.of(2012));
        //obrigacao.setMes(Month.MAY);
        new UtilsValidatorTest().moverPorTipo(obrigacao);
    }

    public void moverPorTipo(Obrigacao obrigacao) throws ParametroNotFoundException, PathInvalidException {
        //montar url
        ObrigacaoContrato contrato = validar(obrigacao);
        //validando por obrigação
        Obrigacao.Tipo tipo = obrigacao.getTipo();
        Path job = Paths.get(obrigacao.getDirForJob());
        Month mesJob = obrigacao.getMes();
        Year anoJob = obrigacao.getAno();

        if(contrato.contains(Periodo.ANO)) {
            String pastaAnoObrigatoria = anoJob == null ? null : contrato.getPastaNome(Periodo.ANO, anoJob, mesJob);
            String pastaMesObrigatoria = mesJob == null ? null : contrato.getPastaNome(Periodo.MES, anoJob, mesJob);
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
                                                log.info("Nome da pasta mes= [" + mes + "] Mes=[" + folderMes.toString()+"]");
                                                log.info("Mes da pasta=[" + mes + "]");
                                                Path novaEstrutura = estrutura.resolve(mes);
                                                log.info("Estrutura pasta mes=[" + novaEstrutura + "]");
                                                Files.list(folderMes).forEach(c -> System.out.println(c));
                                            } catch (ParametroNotFoundException e) {
                                                log.error(e.getMessage());
                                            } catch (ParametroIncorretoException | IOException e) {
                                                log.error(e.getMessage());
                                            }
                                        });
                            } else {
                                System.out.println(folderAno);
                            }
                        } catch (ParametroNotFoundException e) {
                            log.error(e.getMessage());
                        } catch (ParametroIncorretoException e) {
                            log.error(e.getMessage());
                        }

                    });
        }

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

    private ObrigacaoContrato validar(Obrigacao obrigacao) throws ParametroNotFoundException, PathInvalidException {
        ObrigacaoContrato ob = ObrigacaoFactory.get(obrigacao);
        Obrigacao.Tipo tipo = obrigacao.getTipo();
        Path dirForJob = Paths.get(obrigacao.getDirForJob());

        if(!dirForJob.getFileName().toString().equals(tipo.getDescricao())) {
            throw new PathInvalidException("O diretorio informado é invalido");
        }
        if(obrigacao.getAno()==null && ob.contains(Periodo.ANO)) {
            throw new ParametroNotFoundException("O parametro ano é obrigatório para essa obrigação");
        }
        return ob;
    }
}
