package com.tiagods.clientesender.service;

import com.tiagods.clientesender.model.ClienteResource;
import com.tiagods.clientesender.model.EmailDto;
import com.tiagods.clientesender.exception.EmailNaoEnviadoException;
import io.reactivex.rxjava3.core.Observable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Slf4j
public class EmailService {

    @Autowired private TemplateEngine htmlTemplateEngine;

    @Autowired JavaMailSender mailSender;

    public Observable<ClienteResource> sendHtmlEmail(ClienteResource clienteResource) {
        return Observable.just(clienteResource)
                .flatMap(e-> {
                    try {
                        EmailDto emailDto = e.getEmailDto();
                        Context ctx = prepareContext(emailDto);
                        MimeMessage mimeMessage = this.mailSender.createMimeMessage();

                        MimeMessageHelper message = prepareMessage(mimeMessage, emailDto);

                        // Create the HTML body using Thymeleaf
                        String htmlContent = this.htmlTemplateEngine.process(emailDto.getTemplateName(), ctx);
                        message.setText(htmlContent, true /* isHtml */);
                        emailDto.setEmailedMessage(htmlContent);

                        log.info("Processing html email request: " + emailDto.toString());

                        //message = prepareStaticResources(message, emailDto);

                        this.mailSender.send(mimeMessage);
                        this.htmlTemplateEngine.clearTemplateCache();

                        clienteResource.setEmailEnviado(true);
                        clienteResource.setEmailDto(emailDto);
                        return Observable.just(clienteResource);
                    } catch (IOException | MessagingException ex) {
                        return Observable.error(new EmailNaoEnviadoException(ex.getMessage()));
                    }
                });
    }

    public EmailDto sendHtmlEmail(String cid, EmailDto emailDto) {
        try {
            log.info("Tracking: [{}]. Enviando email=({})", cid, emailDto);

            Context ctx = prepareContext(emailDto);
            MimeMessage mimeMessage = this.mailSender.createMimeMessage();

            MimeMessageHelper message = prepareMessage(mimeMessage, emailDto);

            // Create the HTML body using Thymeleaf
            String htmlContent = this.htmlTemplateEngine.process(emailDto.getTemplateName(), ctx);
            message.setText(htmlContent, true /* isHtml */);
            emailDto.setEmailedMessage(htmlContent);

            log.info("Tracking: [{}]. Processing html email request: ", cid, emailDto.toString());

            //message = prepareStaticResources(message, emailDto);

            this.mailSender.send(mimeMessage);
            this.htmlTemplateEngine.clearTemplateCache();

            log.info("Tracking: [{}]. Email enviado com sucesso, email=({})", cid, emailDto);

            return emailDto;
        } catch (IOException | MessagingException ex) {
            log.error("Tracking: [{}].Erro ao tentar enviar email=({}), error=({})", cid, emailDto, ex);
            return null;
        }
    }

    private MimeMessageHelper prepareMessage(MimeMessage mimeMessage, EmailDto emailDto)
            throws MessagingException, IOException {

        // Prepare message using a Spring helper
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                "UTF-8");
        message.setSubject(emailDto.getAssunto());
        message.setFrom(emailDto.getDe());
        message.setTo(emailDto.getPara());

        if (emailDto.getCc() != null && emailDto.getCc().length > 0 && (Arrays.asList(emailDto.getCc()).stream().filter(StringUtils::hasText).count() > 0)) {
            message.setCc(emailDto.getCc());
        }

        if (emailDto.getBcc() != null && emailDto.getBcc().length > 0 && (Arrays.asList(emailDto.getBcc()).stream().filter(StringUtils::hasText).count() > 0)) {
            message.setBcc(emailDto.getBcc());
        }

        if (emailDto.isHasAttachment()) {
            Iterator<Map.Entry<String, Path>> iterator = emailDto.getAttachs().entrySet().iterator();
            while(iterator.hasNext()) {
                var result = iterator.next();
                message.addAttachment(result.getKey(), result.getValue().toFile());
            }

            String[] outros = emailDto.getOutrosAnexos().split(";");
            for(String p : outros) {
                if(StringUtils.hasText(p)){
                    Path path = Paths.get(p.trim());
                    message.addAttachment(path.getFileName().toString(), path.toFile());
                }
            }

            /*
            List<File> attachments = loadResources(emailDto.getPathToAttachment() + "/*" + emailDto.getAttachmentName() + "*.*");
            for (File file : attachments) {
                message.addAttachment(file.getName(), file);
            }
             */
        }

        return message;
    }

    private Context prepareContext(EmailDto emailDto) {
        Context ctx = new Context();
        Set<String> keySet = emailDto.getParameterMap().keySet();
        keySet.forEach(s -> ctx.setVariable(s, emailDto.getParameterMap().get(s)));

        Set<String> resKeySet = emailDto.getStaticResourceMap().keySet();
        resKeySet.forEach(s -> ctx.setVariable(s, emailDto.getStaticResourceMap().get(s)));
        return ctx;
    }

    private MimeMessageHelper prepareStaticResources(MimeMessageHelper message, EmailDto emailDto) throws MessagingException {
        Map<String, Object> staticResources = emailDto.getStaticResourceMap();
        for (Map.Entry<String, Object> entry : staticResources.entrySet()) {
            ClassPathResource imageSource = new ClassPathResource("static/" + (String) entry.getValue());
            message.addInline(entry.getKey(), imageSource, "image/png");
            message.addInline((String) entry.getValue(), imageSource, "image/png");
        }

        return message;
    }

    private List<File> loadResources(String fileNamePattern) throws IOException {
        PathMatchingResourcePatternResolver fileResolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = null;

        try {
            resources = fileResolver.getResources("file:" + fileNamePattern);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        List<File> attachFiles = new ArrayList<>();

        for (Resource resource : resources) {
            attachFiles.add(resource.getFile());
        }

        return attachFiles;
    }
}
