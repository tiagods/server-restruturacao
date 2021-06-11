package com.tiagods.clientesender.service;

import com.tiagods.clientesender.config.EmailConfig;
import com.tiagods.clientesender.config.FileConfig;
import com.tiagods.clientesender.config.RegexConfig;
import com.tiagods.clientesender.model.ClienteResource;
import com.tiagods.clientesender.model.EmailDto;
import com.tiagods.clientesender.model.FileResource;
import com.tiagods.clientesender.model.ProcessoEnum;
import com.tiagods.clientesender.model.Cliente;
import com.tiagods.clientesender.model.NotificacaoControle;
import com.tiagods.clientesender.model.NotificacaoEvento;
import com.tiagods.clientesender.repository.ClienteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClienteService {

    @Autowired FileConfig fileConfig;
    @Autowired RegexConfig regexConfig;
    @Autowired EmailService emailService;
    @Autowired NotificacaoControleService notificacaoControleService;
    @Autowired NotificacaoEventoService notificacaoEventoService;
    @Autowired EmailConfig emailConfig;
    @Autowired ClienteRepository clienteRepository;

    long totalEnvios = 0;

    public List<Cliente> listarClientes() {
        return Arrays.asList(
                //Cliente.builder().idCliente(17l).email("tiagoice@hotmail.com, jamttom@gmail.com").build(),
                //Cliente.builder().idCliente(228l).email("tiagoice@hotmail.com").build(),
                //Cliente.builder().idCliente(479l).email("tiagoice@hotmail.com").build(),
                //Cliente.builder().idCliente(2344l).email("tiagoice@hotmail.com").build()
        );
    }

    public List<Cliente> listarTodosClientes() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarClientePorId(String apelido) {
        return listarTodosClientes()
                .stream()
                .filter(f -> f.getApelido().equals(apelido))
                .findFirst();
    }

    //start do processo
    public void iniciar(String cid) {
        for (ProcessoEnum pe : ProcessoEnum.values()) {
            log.info("Tracking: [{}]. Iniciando busca de arquivos por processo=({})", cid, pe);
            inicarPorProcesso(cid, pe);
        }
    }

    public void inicarPorProcesso(String cid, ProcessoEnum pe) {
        var path = fileConfig.getOrigem().get(pe);
        var regex = regexConfig.getRegex().get(pe);
        var fileResource = FileResource.build(cid, Paths.get(path), pe, regex);

        var arquivosClientes = obterArquivosPorClientes(fileResource);

        log.info("Tracking: [{}]. Iniciando busca de arquivos no processo=({}), pasta=({})", cid, pe, fileResource.getDir());

        if (arquivosClientes == null) {
            log.warn("Tracking: [{}]. Busca nao mapeou arquivos, processo=({}), pasta=({})", cid, fileResource.getDir());
            return;
        }

        processarArquivosPorCliente(arquivosClientes, fileResource);
    }


    private Map<String, Set<Path>> obterArquivosPorClientes(FileResource fileResource) {
        String cid = fileResource.getCid();
        var dir = fileResource.getDir();

        log.info("Tracking: [{}]. Iniciando busca de arquivos na pasta=({}), process=({})", cid, fileResource.getDir(), fileResource.getProcessoEnum());

        Map<String, Set<Path>> arquivosClientes;

        try {
            arquivosClientes = Files.walk(dir)
                    .filter(f ->
                            Files.isRegularFile(f) &&
                            f.getFileName().toString().matches(regexConfig.getInitById()) &&
                            f.getFileName().toString().toLowerCase().matches(fileResource.getRegex()))
                    .collect(Collectors.groupingBy(file -> {
                        Pattern pattern = Pattern.compile(regexConfig.getInitById());
                        Matcher matcher = pattern.matcher(file.getFileName().toString().toLowerCase());
                        matcher.find();
                        String apelido = matcher.group(1);
                        return apelido;
                        }, Collectors.toSet()));

            log.info("Tracking: [{}]. Total de clientes a processar na pasta=({})", cid, arquivosClientes.size());
            return arquivosClientes;
        } catch (IOException err) {
            log.error("Tracking: [{}]. Erro na busca de arquivos na pasta=({}), erro=({})", cid, fileResource.getDir(), err);
            return null;
        }
    }

    public void processarArquivosPorCliente(Map<String, Set<Path>> arquivosClientes, FileResource fileResource) {
        arquivosClientes.entrySet().forEach(stringSetEntry -> {

            var cid = fileResource.getCid();
            var pe = fileResource.getProcessoEnum();
            log.info("Tracking: [{}]. Buscando cliente do processo=({}), cliente=({}), arquivos({})", cid, pe, stringSetEntry.getKey(), stringSetEntry.getValue());

            String clientApelido = stringSetEntry.getKey();
            Optional<Cliente> result = buscarClientePorId(clientApelido);
            if(result.isPresent()) {
                var cliente = result.get();
                prepararClienteParaEnvio(cliente,stringSetEntry.getValue(), fileResource);

            } else {
                log.error("Tracking: [{}]. Falha ao tentar localizar o cliente=({}), processo({}), arquivos=({})", cid, stringSetEntry.getKey(), pe, stringSetEntry.getValue());
            }
        });
    }

    public void prepararClienteParaEnvio(Cliente cliente, Set<Path> arquivos, FileResource fileResource) {
        var cid = fileResource.getCid();

        log.info("Tracking: [{}]. Preparando envio do cliente=({})", cid, cliente);

        var cliResource = ClienteResource.build(cid, cliente, fileResource.getProcessoEnum());
        cliResource.setArquivos(arquivos);

        var result = notificacaoControleService.buscaControle(cliente.getIdCliente(), cliResource.getProcesso());
        if(result.isPresent()) {
            var controle = result.get();
            log.warn("Tracking: [{}]. Analisando controle do cliente=({})", cid, cliente.getIdCliente(), controle);
            if(controle.getMaxEnvios() == controle.getNumEnvios() || !controle.isContinuarEnvios()) {
                log.warn("Tracking: [{}]. Todos os envios ja realizados para o cliente=({}) ou desativar envios=({})", cid, cliente.getIdCliente(), controle.isContinuarEnvios());
                moverArquivos(cliResource);
            } else {
                int numProximoEnvio = controle.getNumEnvios() + 1;
                controle.setNumEnvios(numProximoEnvio);
                cliResource.setControle(controle);

                enviarEmail(cliResource);
            }
        } else {
            var controle = new NotificacaoControle();
            controle.setClienteId(cliente.getIdCliente());
            controle.setProcesso(cliResource.getProcesso());
            controle.setMaxEnvios(fileResource.getProcessoEnum().getEnvios());
            cliResource.setControle(controle);
            enviarEmail(cliResource);
        }
    }

    public void enviarEmail(ClienteResource cliResource) {
        var pe2 =  cliResource.getProcessoEnum();
        var cid = cliResource.getCid();
        var cliente = cliResource.getCliente();

        EmailDto email = montarEmailDto(cid, cliResource, cliResource.getControle(), pe2);
        cliResource.setEmailDto(email);
        cliResource.setTotalContatos(totalEnvios, email);

        var controle = cliResource.getControle();
        var dataParaEnvio = recuperarDataEnvio(cid, pe2, controle);
        var dataValida = validarDataParaEnvio(cid, LocalDate.now(), dataParaEnvio, pe2, controle);

        if(cliResource.getTotalContatos() > 99l) {
            log.error("Tracking: [{}]. Limite de envios ultrapassa o tamanho permitido, atual=({}), proximo=({})", cid, totalEnvios, cliResource.getTotalContatos());
            throw new RuntimeException("Limites de envios ultrapassado");
        }

        if(email.getPara().length == 0 || cliResource.getArquivos().isEmpty() || (Arrays.asList(email.getPara()).stream().filter(StringUtils::hasText).count() == 0)) {
            log.warn("Tracking: [{}]. Falha ao tentar liberar envio dos emails do cliente=({}), emails_cadastrados({}), arquivos=({})", cid, cliente, Arrays.asList(email.getPara()), cliResource.getArquivos());
        } else if(dataValida){
            var emailDto = emailService.sendHtmlEmail(cid, cliResource.getEmailDto());

            if(emailDto != null) {
                totalEnvios = cliResource.getTotalContatos();
                controle = notificacaoControleService.salvar(controle);
                var notificacao = new NotificacaoEvento(controle.getId(), cliResource.getCliente(), cliResource.getProcesso(), cliResource.getEmailDto());
                notificacaoEventoService.salvar(notificacao);
                if(controle.getMaxEnvios() == controle.getNumEnvios()) {
                    moverArquivos(cliResource);
                }
            }
        } else {
            log.warn("Tracking: [{}]. Envio nao sera feito, dataUltimoEnvio=({}), dataMinima=({})", cid, controle.getDataFormatada(), dataParaEnvio);
        }
    }

    public LocalDate recuperarDataEnvio(String cid, ProcessoEnum pe, NotificacaoControle controle) {
        log.info("Tracking: [{}]. Iniciando validacao de data para envio do cliente=({}), controle=({}), processo=({})", cid, controle.getClienteId(), controle, pe);

        if(controle.getData() == null || controle.getNumEnvios() == 1|| pe.getEnvios() == 1) {
            log.info("Tracking: [{}]. Data envio liberada do cliente=({}), controle=({}), processo=({})", cid, controle.getClienteId(), controle, pe);
            return LocalDate.now();
        }

        var dataUltimoEnvio = controle.getData();

        int dias = 0;
        switch (controle.getNumEnvios()) {
            case 2:
                dias = pe.getEnvio2();
                break;
            case 3:
                dias = pe.getEnvio3();
                break;
            default:
                dias = 0;
        }

        LocalDate dateTime = dataUltimoEnvio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate proximaDataLiberada = dateTime.plusDays(dias);

        log.info("Tracking: [{}]. Data minima para envio para o cliente=({}), data_minima=({}), controle=({}), processo=({}), numEnvios=({})", cid, controle.getClienteId(), proximaDataLiberada, controle, pe, controle.getNumEnvios());

        return proximaDataLiberada;
    }

    public boolean validarDataParaEnvio(String cid, LocalDate dataAtual, LocalDate dataLiberada, ProcessoEnum pe, NotificacaoControle controle) {
        if(dataLiberada.isEqual(dataAtual) || dataAtual.isAfter(dataLiberada)) {
            log.info("Tracking: [{}]. Liberando envio do cliente=({}), processo=({})", cid, controle.getClienteId(), pe);
            return true;
        } else {
            log.warn("Tracking: [{}]. Envio bloqueado do cliente=({}), envio deve ser feito na data=({})", cid, controle.getClienteId(), dataLiberada);
            return false;
        }
    }

    public EmailDto montarEmailDto(String cid, ClienteResource clienteResource, NotificacaoControle controle, ProcessoEnum pe2) {
        Cliente cliente = clienteResource.getCliente();
        log.info("Tracking: [{}]. Criando email do cliente=({}), processo=({})", cid, cliente, pe2);

        var de = emailConfig.getDe().get(pe2);
        var cc = emailConfig.getCc().get(pe2);
        var assunto = cliente.getApelido() +" - "+ montarAssunto(controle, pe2);

        Map<String, Path> anexos = new HashMap<>();
        for (Path arquivo : clienteResource.getArquivos()) {
            anexos.put(arquivo.getFileName().toString(), arquivo);
        }
        
        var outrosAnexos = emailConfig.getOutrosArquivos().get(pe2);
        var template = template(pe2, controle.getNumEnvios());

        var email = new EmailDto(de, cliente.getEmail(), cc, assunto, "");
        email.setTemplateName(template);
        email.setAttachs(anexos);
        email.setOutrosAnexos(outrosAnexos);

        log.info("Tracking: [{}]. Email do cliente=({}), email=({})", cid, cliente, email);
        return email;
    }

    public String montarAssunto(NotificacaoControle controle, ProcessoEnum pe) {
        var assunto = "";
        switch (controle.getNumEnvios()) {
            case 2:
                assunto = emailConfig.getAssunto2().get(pe);
                break;
            case 3:
                assunto = emailConfig.getAssunto3().get(pe);
                break;
            default:
                assunto = emailConfig.getAssunto1().get(pe);
        }
        return assunto;
    }

    public String template(ProcessoEnum pe, int numeroDoEnvio) {
        String template;

        switch (numeroDoEnvio) {
            case 2:
                template = emailConfig.getTemplate2().get(pe);
                break;
            case 3:
                template = emailConfig.getTemplate3().get(pe);
                break;
            default:
                template = emailConfig.getTemplate1().get(pe);
                break;
        }
        return template;
    }

    public void moverArquivos(ClienteResource clienteResource) {
        var stringDestino = fileConfig.getDestino().get(clienteResource.getProcessoEnum());
        var pathDestino = Paths.get(stringDestino);
        var cid = clienteResource.getCid();

        /*
        try {
            if (Files.notExists(pathDestino)) {
                Files.createDirectory(pathDestino);
            }
            for (Path arquivo : clienteResource.getArquivos()) {
                var destino = pathDestino.resolve(arquivo.getFileName());
                log.info("Tracking: [{}]. Iniciando movimentacao de arquivos do cliente=({}), de=({}), para=({}))", cid, clienteResource.getCliente().getIdCliente(), arquivo.toString(), destino.toString());
                Files.move(arquivo, destino, StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (IOException err) {
            log.error("Tracking: [{}]. Erro ao mover arquivos do cliente=({}), erro=({})", cid, clienteResource.getCliente().getIdCliente(), err);
        }
         */
    }
}
