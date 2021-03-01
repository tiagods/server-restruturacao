package com.tiagods.obrigacoes.schedules;

import com.tiagods.obrigacoes.service.ClienteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class ClientesJob {

    @Autowired private ClienteService clienteService;

    //realizar as segundas, quartas e sextas
    @Scheduled(cron = "  0 0 1 ? * MON,WED,FRI")
    public void organizarCliente(){
        String cid = UUID.randomUUID().toString();
        log.info("Correlation: [{}]. Iniciando processo - organizacao de pastas de clientes", cid);
        clienteService.inicializarPathClientes(cid, null, true, false);
        log.info("Correlation: [{}]. Processo de organizacao de pastas de cliente concluido.", cid);
    }
}
