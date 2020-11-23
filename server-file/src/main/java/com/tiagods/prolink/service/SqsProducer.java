package com.tiagods.prolink.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tiagods.prolink.config.SQSUrl;
import com.tiagods.prolink.dto.ArquivoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class SqsProducer {

    @Autowired
    QueueMessagingTemplate messagingTemplate;

    @Autowired
    SQSUrl sqs;

    public void sendArquivo(String cid, ArquivoDTO arquivoDTO) {
        ObjectMapper Obj = new ObjectMapper();
        try {
            log.info("Correlation [{}]. Enviando mensagem para: {}, parametros: ({})", cid, sqs.getArquivo(), arquivoDTO);
            String jsonStr = Obj.writeValueAsString(arquivoDTO);
            messagingTemplate.convertAndSend(sqs.getArquivo(), jsonStr);
        } catch (IOException e) {
            log.error("Correlation [{}]. Erro ao enviar mensagem para a fila: {}", cid, sqs.getArquivo());
        }
    }
}
