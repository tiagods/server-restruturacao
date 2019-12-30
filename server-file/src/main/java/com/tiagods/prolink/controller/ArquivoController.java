package com.tiagods.prolink.controller;

import com.tiagods.prolink.model.PathJob;
import com.tiagods.prolink.utils.MoverPastas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/arquivo")
public class ArquivoController {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MoverPastas moverPastas;

    @PostMapping("/moverPastas")
    public ResponseEntity<?> mover(@RequestBody @Valid PathJob pathJob) throws Exception {
        moverPastas.moveByFolder(pathJob);
        return ResponseEntity.noContent().build();
    }
}
