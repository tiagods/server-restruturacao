package com.tiagods.prolink.controller;

import com.tiagods.prolink.exception.InvalidNickException;
import com.tiagods.prolink.model.PathJob;
import com.tiagods.prolink.model.Obrigacao;
import com.tiagods.prolink.obrigacao.ObrigacaoContrato;
import com.tiagods.prolink.service.ObrigacaoPreparedService;
import com.tiagods.prolink.utils.ContextHeaders;
import com.tiagods.prolink.validation.PathLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Month;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<?> mover(@RequestHeader MultiValueMap<String, String> headers, @RequestBody @Valid PathJob pathJob) throws Exception {
        String cid = ContextHeaders.getCid(headers);
        Map<String, Object> parametros = new HashMap<>(){{
            put("pathJob", pathJob.getDirForJob());
            put("estrutura", pathJob.getEstrutura());
        }};
        log.info("Correlation: [{}] GET api/files/moverpastas .Parametros: ({})", cid, parametros);
        moverpastas.iniciarMovimentacaoPorPasta(cid, pathJob,null);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{apelido}/moverpastas")
    public ResponseEntity<?> mover(@RequestHeader MultiValueMap<String, String> headers, @RequestBody @Valid PathJob pathJob,
                                   @PathVariable String apelido) throws Exception {
        String cid = ContextHeaders.getCid(headers);
        Map<String, Object> parametros = new HashMap<>(){{
            put("pathJob", pathJob.getDirForJob());
            put("estrutura", pathJob.getEstrutura());
        }};
        log.info("Correlation: [{}] GET api/files/{}/moverpastas .Parametros: ({})", cid, apelido, parametros);

        if(apelido.length()==4) {
            moverpastas.iniciarMovimentacaoPorPasta(cid, pathJob, apelido);
            return ResponseEntity.noContent().build();
        }
        throw new InvalidNickException("O apelido informado é invalido, tamanho minimo de 4 caracteres");
    }

    @PostMapping("/moverpastas/obrigacao")
    public ResponseEntity<?> moverPorTipo(@RequestHeader MultiValueMap<String, String> headers,
                                          @RequestBody @Valid Obrigacao obrigacao) throws Exception {
        String cid = ContextHeaders.getCid(headers);

        Map<String, Object> parametros = new HashMap<>(){{
            put("dirForJob", obrigacao.getDirForJob());
            put("cliente", obrigacao.getCliente());
            put("tipo", obrigacao.getTipo());
            put("ano", obrigacao.getAno());
            put("mes", obrigacao.getMes());
        }};

        log.info("Correlation: [{}] GET api/files/moverpastas/obrigacao .Parametros: ({})", cid, parametros);

        ObrigacaoContrato contrato = moverpastas.validarObrigacao(cid, obrigacao);
        if(contrato!=null) {
            moverpastas.iniciarMovimentacaoPorObrigacao(cid, contrato, obrigacao);
        } else {
            return ResponseEntity.badRequest().body("{\"message\":\"Obrigação invalida\"}");
        }
        return ResponseEntity.status(HttpStatus.OK).body("{\"message\": \"Solicitação em andamento\"}");
    }
}
