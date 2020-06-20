package com.tiagods.prolink.controller;

import com.tiagods.prolink.exception.InvalidNickException;
import com.tiagods.prolink.model.PathJob;
import com.tiagods.prolink.model.Obrigacao;
import com.tiagods.prolink.obrigacao.ObrigacaoContrato;
import com.tiagods.prolink.service.ObrigacaoPreparedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/files")
public class ArquivoController {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ObrigacaoPreparedService moverpastas;

    @GetMapping("/obrigacoes")
    public ResponseEntity<?> listarObrigacoes() throws Exception {
        return ResponseEntity.ok().body(Obrigacao.Tipo.values());
    }

    @PostMapping("/moverpastas")
    public ResponseEntity<?> mover(@RequestBody @Valid PathJob pathJob) throws Exception {
        moverpastas.iniciarMovimentacaoPorPasta(pathJob,null);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("{apelido}/moverpastas")
    public ResponseEntity<?> mover(@RequestBody @Valid PathJob pathJob,
                                   @PathVariable String apelido) throws Exception {
        if(apelido.length()==4) {
            moverpastas.iniciarMovimentacaoPorPasta(pathJob, apelido);
            return ResponseEntity.noContent().build();
        }
        throw new InvalidNickException("O apelido informado é invalido, tamanho minimo de 4 caracteres");
    }

    @PostMapping("/moverpastas/obrigacao")
    public ResponseEntity<?> moverPorTipo(@RequestBody @Valid Obrigacao obrigacao) throws Exception {
        ObrigacaoContrato contrato = moverpastas.validarObrigacao(obrigacao);
        if(contrato!=null) {
            moverpastas.iniciarMovimentacaoPorObrigacao(contrato, obrigacao);
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.OK).body("{\"message\": \"Solicitação em andamento\"}");
    }
}
