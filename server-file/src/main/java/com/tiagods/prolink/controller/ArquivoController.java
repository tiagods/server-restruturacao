package com.tiagods.prolink.controller;

import com.tiagods.prolink.utils.MoverPastas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api")
public class ArquivoController {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MoverPastas moverPastas;

    @GetMapping("/moverPasta")
    public ResponseEntity<?> mover(@RequestBody String structure,
                                      @RequestBody String dirForJob
                            ){
        Path structureBase = Paths.get(structure);
        Path base = Paths.get(dirForJob);
        log.info(structureBase.toString());
        log.info(base.toString());
        return ResponseEntity.noContent().build();
        //moverPastas.moveByFolder(base,structureBase);
    }
}
