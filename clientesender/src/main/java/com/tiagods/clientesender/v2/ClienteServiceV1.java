package com.tiagods.clientesender.v2;

import com.tiagods.clientesender.config.EmailConfig;
import com.tiagods.clientesender.config.FileConfig;
import com.tiagods.clientesender.config.RegexConfig;
import com.tiagods.clientesender.model.ClienteResource;
import com.tiagods.clientesender.model.EmailDto;
import com.tiagods.clientesender.model.FileResource;
import com.tiagods.clientesender.model.ProcessoEnum;
import com.tiagods.clientesender.exception.*;
import com.tiagods.clientesender.model.Cliente;
import com.tiagods.clientesender.service.EmailService;
import com.tiagods.clientesender.service.NotificacaoEventoService;
import io.reactivex.rxjava3.core.Observable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClienteServiceV1 {

    @Autowired private FileConfig fileConfig;
    @Autowired private RegexConfig regexConfig;
    @Autowired private EmailService emailService;
    @Autowired private NotificacaoEventoService notificacaoEventoService;
    @Autowired private EmailConfig emailConfig;

    List<Cliente> clientes = Arrays.asList(
            //Cliente.builder().idCliente(1l).build(),
            //Cliente.builder().idCliente(2222l).build(),
            //Cliente.builder().idCliente(202l).build(),
            //Cliente.builder().idCliente(22l).build()
    );

    public List<Cliente> listarClientes() {
        return clientes;
    }
    /*
        Pegar arquivos da pasta
        Buscar arquivos por cliente e processar
     */

    public Observable<ClienteResource> persistir(ClienteResource clienteResource) {
        var cid = clienteResource.getCid();
        log.info("Tracking: [{}]. Persistindo EmailEnviado ({})", cid, clienteResource.getEmailDto());

        return Observable.just(clienteResource);
    }

    int totalEnvios = 0;

    public void run(String cid, List<Cliente> clientes) {
        totalEnvios = 0;
        log.info("Tracking: [{}]. Criando FileResouce=({}), Total Clientes=({})", cid, clientes.size());

        Observable.fromArray(ProcessoEnum.values())
                .flatMap(enume-> buildFileResource(cid, enume))
                .flatMap(resource -> processarPorTipo(resource));
    }

    public Observable<FileResource> buildFileResource(String cid, ProcessoEnum processoEnum) {
        log.info("Tracking: [{}]. Criando FileResource por Processo=({})", cid, processoEnum);

        var path = fileConfig.getOrigem().get(processoEnum);
        var regex = regexConfig.getRegex().get(processoEnum);
        var fileResource = FileResource.build(cid, Paths.get(path), processoEnum, regex);

        return Observable.just(fileResource);
    }

    public Observable<FileResource> processarPorTipo(FileResource resource) {
        var cid = resource.getCid();
        log.info("Tracking: [{}]. Criando FileResouce=({})", cid, resource);

        return Observable.just(resource)
                .flatMap(f-> filtrarArquivos(f))
                .flatMap(result -> validarSeVazio(result))
                .flatMap(r-> prossessarClientes(r))
                .map(c-> resource)
                .onErrorResumeNext( ex -> {
                    if(ex instanceof PastaVaziaException) {
                        return Observable.just(resource);
                    }
                    return Observable.error(new Exception("Erro ao processar por tipo"));
                });
    }

    //capturar todos os pdf da pasta
    public Observable<FileResource> filtrarArquivos(FileResource fileResource) {
        var cid = fileResource.getCid();
        log.info("Tracking: [{}]. Iniciando busca de arquivos na pasta=({})", cid, fileResource.getDir());

        return Observable.just(fileResource)
                .flatMap(r-> {
                    List<Path> stream = Files.walk(r.getDir()).filter(Files::isRegularFile)
                            .filter(file-> file.getFileName().toString().toLowerCase().matches(fileResource.getRegex()))
                            .collect(Collectors.toList());

                    r.setArquivos(stream);

                    log.info("Tracking: [{}]. Total de arquivos encontrados na pasta=({})", cid, r.getArquivos().size());
                    return Observable.just(r);
                });
    }

    public Observable<FileResource> validarSeVazio(FileResource resource) {
        if(resource.getArquivos().isEmpty()){
            log.error("Tracking: [{}]. Erro, nao existe arquivos na pasta", resource.getCid());
            return Observable.error(new PastaVaziaException("Pasta vazia "+resource.getDir()));
        } else {
            return Observable.just(resource);
        }
    }

    public Observable prossessarClientes(FileResource resource) {
        var cid = resource.getCid();

        return Observable.fromIterable(resource.getClientes())
                .flatMap(c-> Observable.just(ClienteResource.build(cid, c, resource.getProcessoEnum())))
                .flatMap(cr-> filtrarArquivosPorCliente(resource, cr))
                .flatMap(c-> buildEmailSeValido(c))
                .flatMap(c -> carregarParamentros(c))
                .flatMap(c-> validarContadorDeEnvios(c))
                .flatMap(clienteResource -> enviarEmail(clienteResource))
                .onErrorResumeNext(ex -> {
                   if(ex instanceof NotificacaoEnviadaException || ex instanceof AnexosNaoEncontradosException
                   || ex instanceof IOException || ex instanceof MessagingException){
                       return Observable.empty();
                   }
                   return Observable.error(ex);
                });
    }

    public Observable<ClienteResource> filtrarArquivosPorCliente(FileResource fileResource, ClienteResource clienteResource) {
        String regex = "^(apelido)+([^0-9].*)?$".replace("apelido", clienteResource.getCliente().getApelido());

        return Observable.just(fileResource.getArquivos())
                .flatMap(st-> {
                    Set<Path> result = st.stream()
                            .filter(f -> f.getFileName().toString().matches(regex))
                            .collect(Collectors.toSet());

                    if(result.isEmpty()) {
                        return Observable.error(new AnexosNaoEncontradosException("Nao tem arquivos para o cliente"));
                    } else {
                        clienteResource.setArquivos(result);
                        return Observable.just(clienteResource);
                    }
                });
    }

    public Observable<ClienteResource> buildEmailSeValido(ClienteResource clienteResource) {
        return notificacaoEventoService.buscarNotificacao(clienteResource);
    }

    public Observable<ClienteResource> carregarParamentros(ClienteResource clienteResource) {
        return Observable.just(clienteResource)
                .map(m-> {
                    var cliente = clienteResource.getCliente();
                    var pe =  clienteResource.getProcessoEnum();
                    var de = emailConfig.getDe().get(pe);
                    var cc = emailConfig.getCc().get(pe);
                    var assunto = emailConfig.getAssunto1().get(pe);
                    var outrosAnexos = emailConfig.getOutrosArquivos().get(pe);
                    var template = emailConfig.getTemplate1().get(pe);

                    var email = new EmailDto(de, cliente.getEmail(), cc, assunto, "");
                    email.setTemplateName(template);
                    email.setOutrosAnexos(outrosAnexos);
                    m.setEmailDto(email);
                    return m;
                });
    }

    public Observable<ClienteResource> validarContadorDeEnvios(ClienteResource clienteResource) {
        return Observable.just(clienteResource)
                .flatMap(c-> {
                    var email = c.getEmailDto();
                    int total = totalEnvios + email.getPara().length + email.getCc().length + email.getBcc().length;
                    c.setTotalContatos(total);

                    if(total >= 100) {
                        return Observable.error(new RuntimeException("Numero de envios excedidos "+total));
                    } else {
                        return Observable.just(c);
                    }
                });
    }

    public Observable<ClienteResource> enviarEmail(ClienteResource clienteResource) {
        var cid = clienteResource.getCid();

        return Observable.just(clienteResource)
                .flatMap(c-> {
                    if(c.getEmailDto().getPara().length == 0){
                        return Observable.error(new DestinoInvalidoException("Email para nao existe"));
                    } else {
                        return Observable.just(c);
                    }
                })
                .flatMap(cr -> emailService.sendHtmlEmail(cr))
                .map(m -> {
                    totalEnvios += m.getTotalContatos();
                    return m;
                })
                .flatMap(f -> persistirSeEnviado(f))
                .onErrorResumeNext( ex -> {
                    if(ex instanceof EmailNaoEnviadoException){
                        clienteResource.setEmailDto(null);
                        return Observable.just(clienteResource);
                    }
                    return Observable.error(ex);
                });
    }

    public Observable<ClienteResource> persistirSeEnviado(ClienteResource clienteResource) {
        if(clienteResource.isEmailEnviado() && clienteResource.getEmailDto() != null) {
            return persistir(clienteResource);
        } else {
            return Observable.just(clienteResource);
        }
    }


}
