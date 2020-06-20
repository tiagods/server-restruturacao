package com.tiagods.prolink.schedules;

import com.tiagods.prolink.utils.TempUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
public class ProlinkDigitalJob {

    public static void main(String[] args) {
        agendarProlinkDigital();
    }

    //rodar as 1:00, 2 e 3 do dia 1
    public static void agendarProlinkDigital() {
        log.info("Iniciando processo - Movendo por agendamento");
        LocalDate date = LocalDate.of(2020,01,01);
        LocalDate dateLast = date.plusMonths(-2);
        String ano = dateLast.format(DateTimeFormatter.ofPattern("yyyy"));
        String format = dateLast.format(DateTimeFormatter.ofPattern("MM-yyyy"));
        Path origem = Paths.get("\\\\plkserver\\Todos Departamentos\\Faturamento\\PROLINK DIGITAL "+format);
        //Path origem = Paths.get("c:\\Temp\\Faturamento\\PROLINK DIGITAL "+format);
        log.info("Pasta de origem=["+origem.toString()+"]");
        Path destino = Paths.get("\\\\plkserver\\Todos Departamentos\\PROLINK DIGITAL\\"+ano, origem.getFileName().toString());
        //Path destino = Paths.get("c:\\Temp\\OK\\PROLINK DIGITAL\\"+ano);
        log.info("Pasta de destino=["+destino.toString()+"]");
        try {
            TempUtils.createFileTemp(origem, true);
            FileUtils.moveDirectoryToDirectory(origem.toFile(), destino.toFile(), true);
        } catch (IOException e) {
            log.error("Erro ao mover pastas: "+origem+" para "+destino+"\n"+e.getMessage());
        }
    }
}
