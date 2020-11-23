package com.tiagods.prolink.schedules;

import com.tiagods.prolink.config.ObrigacaoConfig;
import com.tiagods.prolink.exception.ParametroNotFoundException;
import com.tiagods.prolink.exception.PathInvalidException;
import com.tiagods.prolink.model.Obrigacao;
import com.tiagods.prolink.obrigacao.ObrigacaoContrato;
import com.tiagods.prolink.obrigacao.ObrigacaoFactory;
import com.tiagods.prolink.obrigacao.Periodo;
import com.tiagods.prolink.service.ObrigacaoPreparedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
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

@Component
@Slf4j
public class ObrigacoesJob {

    @Autowired
    private ObrigacaoPreparedService moverpastas;
    @Autowired
    private ObrigacaoConfig obrigacaoConfig;

    public static void main(String[] args) {
        LocalDate date = LocalDate.now();
        Set<LocalDate> list = new TreeSet<>();
        //vai processar os 12 ultimos meses, pegando de 2 meses para tras.
        //ex: se estamos em novembro, ira processar
        for(int i = -2; i > -14; i--) {
            list.add(date.plusMonths(i));
        }
    }
    //todos os dias para obrigacoes mensais

    //@Scheduled(cron = "0 0 1,23 1 * ?")
    public void executar() {
        String cid = UUID.randomUUID().toString();
        log.info("Correlation: [{}]. Iniciando processo - Movendo por agendamento", cid);
        LocalDate date = LocalDate.now();
        Set<LocalDate> list = new TreeSet<>();
        //vai processar os 12 ultimos meses, pegando de 2 meses para tras.
        //ex: se estamos em novembro, ira processar
        for(int i = -2; i > -14; i--) {
            list.add(date.plusMonths(i));
        }

        obrigacaoConfig.getObrigacoes().forEach((key,value)->{

            Obrigacao.Tipo tipo = key;
            Path diretorio = Paths.get(value);
            boolean existe = Files.exists(diretorio);
            boolean contemArquivos = false;

            try {
                contemArquivos = Files.isDirectory(diretorio) && StringUtils.hasText(value) && Files.list(diretorio).count() > 0;
            } catch(IOException ex) {
                log.error("Correlation: [{}]. Nao foi possivel listar a pasta {}", cid, value);
            }

            if(key != null && existe && contemArquivos) {
                for (LocalDate localDate : list) {
                    Obrigacao obrigacao = new Obrigacao();
                    obrigacao.setMes(localDate.getMonth());
                    obrigacao.setAno(Year.of(localDate.getYear()));
                    obrigacao.setTipo(key);
                    obrigacao.setDirForJob(value);
                    ObrigacaoContrato contrato = ObrigacaoFactory.get(obrigacao);
                    if(contrato.contains(Periodo.MES)) {
                        try {
                            moverpastas.iniciarMovimentacaoPorObrigacao(cid, contrato, obrigacao);
                        } catch (ParametroNotFoundException e) {
                            e.printStackTrace();
                        } catch (PathInvalidException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
         });
    }
}
