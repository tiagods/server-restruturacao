package com.tiagods.prolink.controller;

import com.tiagods.prolink.model.PathJob;
import com.tiagods.prolink.service.ActionProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/files")
public class ArquivoController {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ActionProcess moveFolders;

    @PostMapping("/moveFolders")
    public ResponseEntity<?> mover(@RequestBody @Valid PathJob pathJob) throws Exception {
        moveFolders.moveByFolder(pathJob);
        return ResponseEntity.noContent().build();
    }
}
