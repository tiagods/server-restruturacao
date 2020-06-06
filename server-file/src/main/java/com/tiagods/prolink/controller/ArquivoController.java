package com.tiagods.prolink.controller;

import com.tiagods.prolink.exception.InvalidNickException;
import com.tiagods.prolink.model.PathJob;
import com.tiagods.prolink.service.ActionProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/files")
public class ArquivoController {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ActionProcess moverpastas;

    @PostMapping("/moverpastas")
    public ResponseEntity<?> mover(@RequestBody @Valid PathJob pathJob) throws Exception {
        moverpastas.moverPorPasta(pathJob,null);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("{nickName}/moverpastas")
    public ResponseEntity<?> mover(@RequestBody @Valid PathJob pathJob,
                                   @PathVariable String nickName) throws Exception {
        if(nickName.length()==4) {
            moverpastas.moverPorPasta(pathJob, nickName);
            return ResponseEntity.noContent().build();
        }
        throw new InvalidNickException("O apelido informado é invalido, tamanho minimo de 4 caracteres");
    }


}
