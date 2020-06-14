package com.tiagods.springbootfile;

import com.tiagods.prolink.exception.ParametroNotFoundException;
import com.tiagods.prolink.exception.PathInvalidException;
import com.tiagods.prolink.model.Obrigacao;
import com.tiagods.prolink.obrigacao.ObrigacaoContrato;
import com.tiagods.prolink.obrigacao.ObrigacaoFactory;
import com.tiagods.prolink.obrigacao.Periodo;
import com.tiagods.prolink.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Month;
import java.time.Year;

@Slf4j
public class UtilsValidatorTest {
    public static void main(String[] args) throws Exception {
        Obrigacao obrigacao = new Obrigacao();
        obrigacao.setTipo(Obrigacao.Tipo.PROLINKDIGITAL);
        obrigacao.setDirForJob("\\\\plkserver\\Todos Departamentos\\PROLINK DIGITAL");
        obrigacao.setAno(Year.of(2015));
        obrigacao.setMes(Month.AUGUST);

        new UtilsValidatorTest().moverPorTipo(obrigacao);
    }

    public void moverPorTipo(@Valid Obrigacao tipo) throws Exception {
        //montar url
        validar(tipo);
        //validando por obrigação
        Obrigacao.Tipo obrigacao = tipo.getTipo();
        Path job = Paths.get(tipo.getDirForJob());

/*
        if(obrigacao.getConfig().contains(Year.class)){
            String addYear = tipo.getAno()==null? null : obrigacao.getPathAno().replace("{ANO}", tipo.getAno().toString());
            log.info("Valor do ano: "+addYear);
            Files.list(job)
                    .filter(filter->
                            addYear==null ?
                                    Files.isDirectory(filter)
                                    : Files.isDirectory(filter) && filter.getFileName().toString().equals(addYear)
                    )
                    .forEach(f -> {
                        if (obrigacao.getConfig().contains(Month.class)) {
                            String ano = f.getFileName().toString();

                            log.info("Nome da pasta ano: "+ano+"\t"+f.toString());
                            //pegar o nome do diretorio e remover qualquer outro adicional do nome para pegar o periodo
                            ano = ano.replace(obrigacao.getPathAno().replace("{ANO}",""),"");

                            log.info("Ano da pasta: "+ano);
                            
                            String estrutura = obrigacao.getEstrutura()+"/"+ano;

                            log.info("Nome da estrutura: "+estrutura);

                            String addMes = tipo.getMes()==null?
                                    null
                                    :
                                    obrigacao.getPathMes().replace("{MES}", DateUtils.mesString(tipo.getMes().getValue()))
                                            .replace("{ANO}", ano);

                            log.info("Capturando mes: "+addMes);
                            try {
                                Files.list(f)
                                        .filter(filter->
                                                addMes==null ?
                                                        Files.isDirectory(filter)
                                                        : Files.isDirectory(filter) && filter.getFileName().toString().equals(addMes)
                                        )
                                        .forEach(t->{
                                            String mes = t.getFileName().toString();

                                            log.info("Nome da pasta mes: "+mes+"\t"+t.toString());
                                            //pegar o nome do diretorio e remover qualquer outro adicional do nome para pegar o periodo
                                            //String extracao = obrigacao.getPathMes().replace("{MES}","").replace("{ANO}","");
                                            //mes = mes.replace(MyStringUtils.substituirCaracteresEspeciais(extracao),"");

                                            log.info("Mes da pasta: "+mes);

                                            String novaEstrutura = estrutura+"/"+mes;
                                            log.info("Estrutura pasta mes: "+novaEstrutura);
                                        });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
        */

    }

    private void validar(Obrigacao obrigacao) throws ParametroNotFoundException, PathInvalidException {
        ObrigacaoContrato ob = ObrigacaoFactory.get(obrigacao);
        Obrigacao.Tipo tipo = obrigacao.getTipo();
        Path dirForJob = Paths.get(obrigacao.getDirForJob());

        if(!dirForJob.getFileName().toString().equals(tipo.getDescricao())) {
            throw new PathInvalidException("O diretorio informado é invalido");
        }
        if(obrigacao.getAno()==null && ob.contains(Periodo.ANO)) {
            throw new ParametroNotFoundException("O parametro ano é obrigatório para essa obrigação");
        }
        if(obrigacao.getMes()==null && ob.contains(Periodo.MES)) {
            throw new ParametroNotFoundException("O parametro mês é obrigatório para essa obrigação");
        }
    }
}
