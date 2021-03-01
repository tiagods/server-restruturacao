package com.tiagods.obrigacoes.controller;

import com.tiagods.obrigacoes.config.ObrigacaoConfig;
import com.tiagods.obrigacoes.exception.InvalidNickException;
import com.tiagods.obrigacoes.model.PathJob;
import com.tiagods.obrigacoes.model.Obrigacao;
import com.tiagods.obrigacoes.obrigacao.ObrigacaoContrato;
import com.tiagods.obrigacoes.service.ObrigacaoPreparedService;
import com.tiagods.obrigacoes.utils.ContextHeaders;
import com.tiagods.obrigacoes.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/files")
public class ArquivoController {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ObrigacaoPreparedService moverpastas;
    @Autowired
    private ObrigacaoConfig obrigacaoConfig;

    @GetMapping("/obrigacoes")
    public ResponseEntity<?> listarObrigacoes(@RequestHeader MultiValueMap<String, String> headers) throws Exception {
        String cid = ContextHeaders.getCid(headers);
        log.info("Correlation: [{}] GET api/files/obrigacoes.", cid);
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
            return ResponseEntity.noContent().header("cid", cid).build();
        }
        throw new InvalidNickException("O apelido informado é invalido, tamanho minimo de 4 caracteres");
    }


    @Async
    @PostMapping("/moverpastas/obrigacao/{tipo}/all")
    public ResponseEntity<?> moverTudoObrigacao(@RequestHeader MultiValueMap<String, String> headers,
                                                @PathVariable @NotNull String tipo) throws Exception {
        String cid = ContextHeaders.getCid(headers);

        log.info("Correlation: [{}] GET api/files/moverpastas/obrigacao/{}/all)", cid, tipo);

        Set<LocalDate> datas = DateUtils.gerarPeriodosProcessamento(null);
        Optional<Map.Entry<Obrigacao.Tipo, String>> result = obrigacaoConfig.getObrigacoes()
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().name().equals(tipo))
                .findFirst();

        if (result.isPresent()) {
            Map.Entry<Obrigacao.Tipo, String> entry = result.get();
            moverpastas.iniciarMovimentacaoPorObrigacaoGeral(cid, entry.getKey(), entry.getValue(), datas);
        } else {
            return ResponseEntity.badRequest().body("{\"message\":\"Obrigação invalida\"}");
        }
        return ResponseEntity.status(HttpStatus.OK).header("cid", cid).body("{\"message\": \"Solicitação em andamento\"}");
    }

    @Async
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

    @GetMapping("/obrigacoes/pastas/clear")
    public void clear() {
        System.gc();
    }
}
