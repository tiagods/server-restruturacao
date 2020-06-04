package com.tiagods.prolink.controller;

import com.tiagods.prolink.config.ServerFile;
import com.tiagods.prolink.exception.StructureNotFoundException;
import com.tiagods.prolink.service.MonitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/monitor")
public class MonitorController {

    @Autowired
    private MonitorService monitor;

    @Autowired
    private ServerFile serverFile;

    @GetMapping(value = "/verifier",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public @ResponseBody byte[] monitorarParaBackup(@RequestBody String pasta){
        if(!Files.isDirectory(Paths.get(pasta)) || Files.notExists(Paths.get(pasta))){
            new StructureNotFoundException("Valor informado nao é um diretorio valido ou não existe");
        }
        return monitor.capturarRelatorio(pasta);
    }
}
