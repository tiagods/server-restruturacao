package com.tiagods.prolink.schedules;

import com.tiagods.prolink.config.ServerFile;
import com.tiagods.prolink.utils.TempUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Component
public class ProlinkDigitalJob {

    @Autowired
    ServerFile serverFile;

    //rodar as 1:00, 2 e 3 do dia 1
    //@Scheduled(cron = "0 0 1,23 1 * ?")
    public void agendarProlinkDigital() {
        String cid = UUID.randomUUID().toString();
        log.info("Correlation: [{}]. Iniciando processo - Movendo por agendamento", cid);
        //LocalDate date = LocalDate.of(2020,01,01);
        LocalDate date = LocalDate.now();
        LocalDate dateLast = date.plusMonths(-2);
        String ano = dateLast.format(DateTimeFormatter.ofPattern("yyyy"));
        String format = dateLast.format(DateTimeFormatter.ofPattern("MM-yyyy"));
        Path origem = Paths.get(serverFile.getRoot()+"\\Todos Departamentos\\Faturamento\\PROLINK DIGITAL "+format);
        log.info("Correlation: [{}]. Pasta de origem=({})", cid, origem.toString());
        Path destino = Paths.get(serverFile.getRoot()+"\\Todos Departamentos\\PROLINK DIGITAL\\"+ano, origem.getFileName().toString());
        log.info("Correlation: [{}]. Pasta de destino=({})", cid, destino.toString());

        if(Files.exists(origem)) {
            try {
                //TempUtils.createFileTemp(origem, true);
                FileUtils.moveDirectoryToDirectory(origem.toFile(), destino.toFile(), true);
                log.info("Correlation: [{}]. Conclusao de movimentacao de arquivos de prolink digital em faturamento", cid, destino.toString());
            } catch (IOException e) {
                log.error("Correlation: [{}]. Erro ao mover pastas: ({}) para: ({}) ex:({})", cid, origem, destino, e.getMessage());
            }
        } else {
            log.warn("Correlation: [{}]. Nao foi possivel mover pasta: ({}) para: ({}). A pasta de origem nao existe",
                    cid, origem, destino);
        }
    }
}
