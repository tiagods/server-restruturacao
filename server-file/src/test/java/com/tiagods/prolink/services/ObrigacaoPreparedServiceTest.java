package com.tiagods.prolink.services;

import com.tiagods.prolink.config.ServerFile;
import com.tiagods.prolink.model.Obrigacao;
import com.tiagods.prolink.obrigacao.ObrigacaoContrato;
import com.tiagods.prolink.obrigacao.ObrigacaoFactory;
import com.tiagods.prolink.obrigacao.Periodo;
import com.tiagods.prolink.repository.ArquivoErroRepository;
import com.tiagods.prolink.repository.ArquivoRepository;
import com.tiagods.prolink.service.ClienteDAOService;
import com.tiagods.prolink.service.ObrigacaoPreparedService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@Slf4j
public class ObrigacaoPreparedServiceTest {

    List<String> clientesJob = Arrays.asList("0009","0027","0105","2223");

    List<Obrigacao> obrigacoesMapeadas = new ArrayList<>();

    @Autowired private ClienteDAOService clienteDAOService;
    @Autowired private ServerFile serverFile;
    @Autowired private ObrigacaoPreparedService service;
    @Autowired private ArquivoRepository arquivoRepository;
    @Autowired private ArquivoErroRepository erroRepository;

    @Test
    public void listarClientes() {
        Assert.assertTrue(clienteDAOService.list().size()>0);
    }

    @Test
    public void moverProlinkDigitalPeriodo() {
        Obrigacao.Tipo prolinkdigital = Obrigacao.Tipo.PROLINKDIGITAL;
        Path job = Paths.get("c:/Temp", prolinkdigital.getDescricao());

        arquivoRepository.deleteAll();
        erroRepository.deleteAll();
        try {
            FileSystemUtils.deleteRecursively(job);
            Obrigacao obrigacao = new Obrigacao();
            obrigacao.setTipo(prolinkdigital);
            obrigacao.setAno(Year.of(2018));
            obrigacao.setMes(Month.AUGUST);
            obrigacao.setCliente(105L);
            obrigacao.setDirForJob(job.toString());

            ObrigacaoContrato contrato = ObrigacaoFactory.get(obrigacao);
            montarPastas(job, contrato, obrigacao);

            service.iniciarMovimentacaoPorObrigacao(contrato, obrigacao);

            validar(obrigacao);
            obrigacoesMapeadas.clear();
        }catch (IOException e) {
            Assert.fail();
        }
    }

    public void validar(Obrigacao obrigacao) {
        obrigacoesMapeadas.forEach(ob-> {
            boolean anoSelecionado = obrigacao.getAno()!=null;
            boolean mesSelecionado = obrigacao.getMes()!=null;
            boolean clienteSelecionado = obrigacao.getCliente()!=null;


            
        });
    }
    public boolean isEmpty(File file) {
        File[] files = file.listFiles();
        return  files.length == 0;
    }

    public void montarPastas(Path pathJob, ObrigacaoContrato contrato, Obrigacao obrigacao) throws IOException {
        if(Files.exists(pathJob)) {
            FileSystemUtils.deleteRecursively(pathJob);
        }
        Set<Integer> anos = Stream.iterate(2018, n -> n+1).limit(3).collect(Collectors.toSet());
        Set<Month> meses = Arrays.asList(Month.values()).stream().collect(Collectors.toSet());

        anos.forEach(a->
                meses.forEach(m-> {
                    Path pasta = Paths.get(pathJob.toString(),
                            contrato.getPastaNome(Periodo.ANO, Year.of(a), m),
                            contrato.getPastaNome(Periodo.MES, Year.of(a), m));

                    Obrigacao b = new Obrigacao(
                            obrigacao.getTipo(),
                            Year.of(a),
                            m,
                            obrigacao.getCliente(),
                            pasta.toString()
                    );
                    obrigacoesMapeadas.add(b);
                    criarArquivosTemporarios(pasta);
                }));

        Assert.assertEquals(2018, anos.stream().min(Integer::compareTo).get().intValue());
        Assert.assertEquals(2020, anos.stream().max(Integer::compareTo).get().intValue());
    }

    public void criarArquivosTemporarios(Path path) {
        clientesJob.forEach(cli -> {
            Stream.iterate(1, n -> n + 1)
                    .limit(10)
                    .forEach(fileName -> {
                        Path cliPath = path.resolve(cli);
                        Path pathFile = cliPath.resolve(fileName + ".txt");
                        try {
                            if (Files.notExists(cliPath)) Files.createDirectories(cliPath);
                            FileUtils.write(pathFile.toFile(), "qualquer coisa", Charset.defaultCharset());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        });
    }

}
