package com.tiagods.prolink.controller;

import com.tiagods.prolink.exception.InvalidNickException;
import com.tiagods.prolink.exception.ParametroNotFoundException;
import com.tiagods.prolink.exception.PathInvalidException;
import com.tiagods.prolink.model.PathJob;
import com.tiagods.prolink.model.Obrigacao;
import com.tiagods.prolink.obrigacao.ObrigacaoContrato;
import com.tiagods.prolink.obrigacao.ObrigacaoFactory;
import com.tiagods.prolink.obrigacao.Periodo;
import com.tiagods.prolink.service.ObrigacaoPreparedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        ObrigacaoContrato contrato = validar(obrigacao);
        if(contrato!=null) {
            moverpastas.iniciarMovimentacaoPorObrigacao(contrato, obrigacao);
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
    }
    private ObrigacaoContrato validar(Obrigacao obrigacao) throws ParametroNotFoundException, PathInvalidException {
        ObrigacaoContrato ob = ObrigacaoFactory.get(obrigacao);
        Obrigacao.Tipo tipo = obrigacao.getTipo();
        Path dirForJob = Paths.get(obrigacao.getDirForJob());
        if(!dirForJob.getFileName().toString().equals(tipo.getDescricao())) {
            String message = "O diretorio informado é invalido para essa obrigação";
            log.error(message);
            throw new PathInvalidException(message);
        }
        if(obrigacao.getAno()==null && ob.contains(Periodo.ANO)) {
            String message = "O parametro ano é obrigatório para essa obrigação";
            log.error(message);
            throw new ParametroNotFoundException(message);
        }
        if(obrigacao.getMes()==null && ob.contains(Periodo.MES)) {
            log.info("Mes não informado para uma obrigação mensal");
            //throw new ParametroNotFoundException("O parametro mês é obrigatório para essa obrigação");
        }
        return ob;
    }
}
