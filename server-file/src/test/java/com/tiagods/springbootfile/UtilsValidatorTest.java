package com.tiagods.springbootfile;

import com.tiagods.prolink.model.Tipo;
import com.tiagods.prolink.exception.ParametroNotFoundException;
import com.tiagods.prolink.exception.PathInvalidException;
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
        Tipo tipo = new Tipo();
        tipo.setObrigacao(Tipo.Obrigacao.PROLINKDIGITAL);
        tipo.setDirForJob("\\\\plkserver\\Todos Departamentos\\PROLINK DIGITAL");
        tipo.setAno(Year.of(2015));
        tipo.setMes(Month.AUGUST);

        new UtilsValidatorTest().moverPorTipo(tipo);
    }

    public void moverPorTipo(@Valid Tipo tipo) throws Exception {
        //montar url
        validar(tipo);
        //validando por obrigação
        Tipo.Obrigacao obrigacao = tipo.getObrigacao();
        Path job = Paths.get(tipo.getDirForJob());
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
    }

    private void validar(Tipo tipo) throws ParametroNotFoundException, PathInvalidException {
        Tipo.Obrigacao ob = tipo.getObrigacao();
        if(!tipo.getDirForJob().endsWith(ob.getDescricao())) {
            throw new PathInvalidException("O diretorio informado é invalido");
        }
        if(tipo.getAno()==null && ob.getConfig().contains(Year.class)) {
            throw new ParametroNotFoundException("O parametro ano é obrigatório para essa obrigação");
        }
        if(tipo.getMes()==null && ob.getConfig().contains(Month.class)) {
            throw new ParametroNotFoundException("O parametro mês é obrigatório para essa obrigação");
        }
    }
}
