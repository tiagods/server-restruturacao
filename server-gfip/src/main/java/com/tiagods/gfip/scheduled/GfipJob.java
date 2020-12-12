package com.tiagods.gfip.scheduled;

import com.tiagods.gfip.services.MapearGfip;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@EnableScheduling
@Component
@Slf4j
public class GfipJob {

    @Autowired
    MapearGfip gfip;

   // @Scheduled(cron = "${job.gfip}")
    public void gfipJob() throws Exception{
        boolean result = gfip.isProcessoRodando();
        String correlation = UUID.randomUUID().toString();
        log.info("Correlation [{}}. Iniciando processo de mapeamento da gfip. Ja existe um processo sendo executando ? {}", correlation, result);
        if(result){
            log.error("Correlation [{}}. Processamento não ocorrerá porque existe uma outra execução no momento", correlation);
        } else {
            gfip.iniciarMapeamento();
        }
    }
}
