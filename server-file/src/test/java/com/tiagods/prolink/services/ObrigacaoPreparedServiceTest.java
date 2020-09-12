package com.tiagods.prolink.services;

import com.jupiter.tools.spring.test.mongo.annotation.ExportMongoDataSet;
import com.jupiter.tools.spring.test.mongo.annotation.MongoDataSet;
import com.jupiter.tools.spring.test.mongo.junit4.BaseMongoIT;
import com.tiagods.prolink.config.ServerFile;
import com.tiagods.prolink.dto.ClienteDTO;
import com.tiagods.prolink.exception.ParametroIncorretoException;
import com.tiagods.prolink.exception.ParametroNotFoundException;
import com.tiagods.prolink.exception.PathInvalidException;
import com.tiagods.prolink.model.Obrigacao;
import com.tiagods.prolink.obrigacao.ObrigacaoContrato;
import com.tiagods.prolink.obrigacao.Periodo;
import com.tiagods.prolink.repository.ArquivoErroRepository;
import com.tiagods.prolink.repository.ArquivoRepository;
import com.tiagods.prolink.repository.ClienteRepository;
import com.tiagods.prolink.dao.ClienteDAOService;
import com.tiagods.prolink.service.ClienteService;
import com.tiagods.prolink.service.ObrigacaoPreparedService;
import com.tiagods.prolink.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Month;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
Documentação spring boot with mongo test
Necessario de docker
https://github.com/jupiter-tools/spring-test-mongo#introduction
 */

@ActiveProfiles("dev")
@Slf4j
public class ObrigacaoPreparedServiceTest extends BaseMongoIT {

    List<String> clientesJob = Arrays.asList("0009","0027","0105","2223");

    List<Obrigacao> obrigacoesMapeadas = new ArrayList<>();

    @Autowired private ClienteService clienteService;
    @Autowired private ServerFile serverFile;
    @Autowired private ObrigacaoPreparedService obrigacaoService;

    @Autowired private ClienteDAOService clienteDAOService;

    @Autowired private ClienteRepository clienteRepository;
    @Autowired private ArquivoRepository arquivoRepository;
    @Autowired private ArquivoErroRepository erroRepository;

    static List<ClienteDTO> lista;

    @BeforeClass
    public static void init() {
        lista = Arrays.asList(
                new ClienteDTO("1234qwer", 9L, "Empresa Teste ME", "OURO", "01.000.111/0001-00", new Date(), false),
                new ClienteDTO("1234qwer1", 27L, "Empresa Teste SA", "OURO", "01.000.111/0001-00", new Date(), false),
                new ClienteDTO("1234qwer12", 105L, "Empresa Teste LTDA", "OURO", "01.000.111/0001-00", new Date(), false),
                new ClienteDTO("1234qwer23", 2223L, "Empresa Teste", "OURO", "01.000.111/0001-00", new Date(), false)
        );
    }

    @Test
    @ExportMongoDataSet(outputFile = "target/dataset/export.json")
    public void salvarLote(){
        clienteRepository.saveAll(lista);
    }

    @Test
    @MongoDataSet(value = "/dataset/clientes.json")
    public void listarClientes() throws Exception{
        Assert.assertTrue(clienteDAOService.list().size()>0);
    }

    Obrigacao montarObrigacaoFakeAnoMesCliente(Obrigacao.Tipo tipo) {
        Path job = Paths.get("c:/Temp", tipo.getDescricao());
        return criarObrigacao(tipo, Year.of(2018), Month.AUGUST, 105L, job.toString());
    }

    Obrigacao montarObrigacaoFakeAnoMes(Obrigacao.Tipo tipo) {
        Path job = Paths.get("c:/Temp", tipo.getDescricao());
        return criarObrigacao(tipo, Year.of(2018), Month.AUGUST, null, job.toString());
    }

    Obrigacao montarObrigacaoFakeAno(Obrigacao.Tipo tipo) {
        Path job = Paths.get("c:/Temp", tipo.getDescricao());
        return criarObrigacao(tipo, Year.of(2018), null, null, job.toString());
    }

    Obrigacao montarObrigacaoFake(Obrigacao.Tipo tipo) {
        Path job = Paths.get("c:/Temp", tipo.getDescricao());
        return criarObrigacao(tipo, null, null, null, job.toString());
    }

    @MongoDataSet(value = "/dataset/clientes.json")
    public void moverPastaClientesEValidar(String cid, Obrigacao obrigacao) throws IOException, ParametroNotFoundException, PathInvalidException, ParametroIncorretoException {
        arquivoRepository.deleteAll();
        erroRepository.deleteAll();
        Path job = Paths.get(obrigacao.getDirForJob());
        Path base = Paths.get(serverFile.getBase());

        FileSystemUtils.deleteRecursively(job);
        FileSystemUtils.deleteRecursively(base);

        Files.createDirectory(base);

        ObrigacaoContrato contrato = obrigacaoService.validarObrigacao(cid, obrigacao);
        montarPastas(job, contrato, obrigacao);
        obrigacaoService.iniciarMovimentacaoPorObrigacao(cid, contrato, obrigacao);
        validarResultado(cid, obrigacao, contrato);
        obrigacoesMapeadas.clear();
    }

    private void validarResultado(String cid, Obrigacao obrigacao, ObrigacaoContrato contrato) {
        obrigacoesMapeadas.forEach(ob-> {
            boolean anoExiste = obrigacao.getAno()!=null;
            boolean mesExiste = obrigacao.getMes()!=null;
            boolean clienteExiste = obrigacao.getCliente()!=null;

            log.info("Monitorando a pasta:"+ob.getDirForJob());

            Path estrutura = Paths.get(ob.getTipo().getEstrutura(),
                    contrato.getPastaNome(Periodo.ANO, ob.getAno(), ob.getMes()),
                    DateUtils.mesString(ob.getMes()));

            if(anoExiste && mesExiste && clienteExiste) { //apenas um cliente de ano e mes definido
                boolean deveSerVazio = ob.getAno().getValue() == obrigacao.getAno().getValue() && ob.getMes() == obrigacao.getMes() && ob.getCliente() == obrigacao.getCliente();
                validacao(cid, ob, deveSerVazio, estrutura);
            }
            else if(anoExiste && mesExiste) { //processar todos os clientes do ano e mes selecionado
                boolean deveSerVazio = ob.getAno().getValue() == obrigacao.getAno().getValue() && ob.getMes() == obrigacao.getMes();
                validacao(cid, ob, deveSerVazio, estrutura);
            }
            else if(anoExiste) { // todos clientes do ano selecionado
                boolean deveSerVazio = ob.getAno().getValue() == obrigacao.getAno().getValue();
                validacao(cid, ob, deveSerVazio, estrutura);
            }
        });
    }

    void validacao(String cid, Obrigacao ob, boolean deveSerVazio, Path estrutura){
        log.info("Pasta Origem: deve ser vazia:"+deveSerVazio);
        boolean origemVazio = seVazio(Paths.get(ob.getDirForJob()));//pasta do cliente origem
        log.info("Pasta Origem: esta vazia:"+origemVazio);
        boolean clienteVazio = pastaClienteVazio(cid, ob, estrutura);
        log.info("Pasta do cliente: esta vazia:"+clienteVazio);

        if(deveSerVazio != origemVazio || origemVazio == clienteVazio){
            Assert.fail();
        }
    }

    //Vai entrar na pasta de cada cliente destino, pasta da obrigação e validar se deve ficar vazio ou nao
    boolean pastaClienteVazio(String cid, Obrigacao obrigacao, Path estrutura) {
        final Path path = clienteService.buscarPastaBaseClientePorId(cid, obrigacao.getCliente());
        log.info("Buscando cliente na base=["+path+"]");
        if(path==null) return true;
        Path pastaObrigacaoCliente = path.resolve(estrutura);
        if(Files.notExists(pastaObrigacaoCliente)) return true;
        log.info("Validando pasta do cliente=["+pastaObrigacaoCliente+"]");
        return seVazio(pastaObrigacaoCliente);
    }

    boolean validandoSeVazioOuNao(Path pasta, boolean deveSerVazio) {
        boolean vazio = seVazio(pasta);
        if(deveSerVazio == vazio) {
            log.info(vazio? "Não " :""+"Existe arquivos na pasta=["+pasta.toString()+"]");
        }
        else {
            log.info("A pasta=["+pasta.toString()+"] "+(vazio?"": "Não ")+"deveria estar vazia");
            Assert.fail();
        }
        return vazio;
    }

    private boolean seVazio(Path path) {
        File[] files = path.toFile().listFiles();
        log.info("Verificando arquivos da pasta=["+path.toString()+"] Quantidade=["+files.length+"]");
        return  files.length == 0;
    }

    //montar pastas que são obrigatorios ano e mes
    private void montarPastas(Path pathJob, ObrigacaoContrato contrato, Obrigacao obrigacao) throws IOException {
        if(Files.exists(pathJob)) {
            FileSystemUtils.deleteRecursively(pathJob);
        }
        Set<Integer> anos = Stream.iterate(2018, n -> n+1).limit(3).collect(Collectors.toSet());
        Set<Month> meses = Arrays.asList(Month.values()).stream().collect(Collectors.toSet());

        if(contrato.contains(Periodo.ANO)) {
            anos.forEach(a-> {
                    String nomePastaAno = contrato.getPastaNome(Periodo.ANO, Year.of(a), null);
                    if(contrato.contains(Periodo.MES)) {
                        meses.forEach(m-> {
                            Path pastaMes = Paths.get(pathJob.toString(), nomePastaAno, contrato.getPastaNome(Periodo.MES, Year.of(a), m));
                            Obrigacao b = criarObrigacao(obrigacao.getTipo(), Year.of(a), m, obrigacao.getCliente(), pastaMes.toString());
                            criarArquivosTemporarios(pastaMes, b);
                        });
                    } else {
                        Path pastaAno = Paths.get(pathJob.toString(), nomePastaAno);
                        Obrigacao b = criarObrigacao(obrigacao.getTipo(), Year.of(a), null, obrigacao.getCliente(), pastaAno.toString());
                        criarArquivosTemporarios(pastaAno,b);
                    }
            });
        } else {
            Obrigacao b =  criarObrigacao(obrigacao.getTipo(), null, null, obrigacao.getCliente(), pathJob.toString());
            criarArquivosTemporarios(pathJob, b);
        }

        Assert.assertEquals(2018, anos.stream().min(Integer::compareTo).get().intValue());
        Assert.assertEquals(2020, anos.stream().max(Integer::compareTo).get().intValue());
    }

    private Obrigacao criarObrigacao(Obrigacao.Tipo tipo, Year ano, Month mes, Long cliente, String pasta) {
        return new Obrigacao(
                tipo,
                ano,
                mes,
                cliente,
                pasta
        );
    }

    private void criarArquivosTemporarios(Path path, Obrigacao obrigacao) {
        clientesJob.forEach(cli -> {
            Path cliPath = path.resolve(cli);
            Obrigacao b = obrigacao;
            b.setDirForJob(cliPath.toString());
            b.setCliente(Long.parseLong(cli));

            if(obrigacoesMapeadas.stream()
                    .filter(pre-> pre.getDirForJob().equalsIgnoreCase(cliPath.toString()))
                    .findFirst().isEmpty()) {
                obrigacoesMapeadas.add(obrigacao);
            }

            Stream.iterate(1, n -> n + 1)
                    .limit(1)
                    .forEach(fileName -> {

                        Path pathFile = cliPath.resolve(fileName + ".txt");
                        try {
                            if (Files.notExists(cliPath)) Files.createDirectories(cliPath);
                            FileUtils.write(pathFile.toFile(), "qualquer coisa", Charset.defaultCharset());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Assert.fail();
                        }
                    });
        });
    }

}
