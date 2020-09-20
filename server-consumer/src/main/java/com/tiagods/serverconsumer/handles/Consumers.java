package com.tiagods.serverconsumer.handles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiagods.serverconsumer.dto.ArquivoDTO;
import com.tiagods.serverconsumer.services.ArquivoDAOService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class Consumers {

    @Autowired
    ArquivoDAOService service;

    @SqsListener("${sqsurl.arquivo}")
    public void receiveMessage(String message, @Header("SenderId") String senderId) throws JsonProcessingException {
        ArquivoDTO arquivoDTO = new ObjectMapper()
                .readValue(message, ArquivoDTO.class);
        log.info("Correlation [{}]. Message: ({})", arquivoDTO.getCorrelation(), arquivoDTO);

        service.save(arquivoDTO);
    }
}
