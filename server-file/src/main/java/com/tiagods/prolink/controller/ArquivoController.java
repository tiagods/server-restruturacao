package com.tiagods.prolink.controller;

import com.tiagods.prolink.exception.InvalidNickException;
import com.tiagods.prolink.exception.ParametroNotFoundException;
import com.tiagods.prolink.exception.PathInvalidException;
import com.tiagods.prolink.job.Ano;
import com.tiagods.prolink.model.PathJob;
import com.tiagods.prolink.service.ActionProcess;
import com.tiagods.prolink.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Month;
import java.time.Year;

@RestController
@RequestMapping("/api/files")
public class ArquivoController {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private ActionProcess moverpastas;

    @GetMapping("/obrigacoes")
    public ResponseEntity<?> listarObrigacoes() throws Exception {
        return ResponseEntity.ok().body(Tipo.Obrigacao.values());
    }

    @PostMapping("/moverpastas")
    public ResponseEntity<?> mover(@RequestBody @Valid PathJob pathJob) throws Exception {
        moverpastas.moverPorPasta(pathJob,null);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("{apelido}/moverpastas")
    public ResponseEntity<?> mover(@RequestBody @Valid PathJob pathJob,
                                   @PathVariable String apelido) throws Exception {
        if(apelido.length()==4) {
            moverpastas.moverPorPasta(pathJob, apelido);
            return ResponseEntity.noContent().build();
        }
        throw new InvalidNickException("O apelido informado é invalido, tamanho minimo de 4 caracteres");
    }

    @PostMapping("/moverpastas/obrigacao")
    public ResponseEntity<?> moverPorTipo(@RequestBody @Valid Tipo tipo) throws Exception {
        //montar url
        validar(tipo);
        //validando por obrigação
        Tipo.Obrigacao obrigacao = tipo.getObrigacao();
        Path job = Paths.get(tipo.getDirForJob());
        if(obrigacao.getConfig().contains(Year.class)){
            String addYear = tipo.getAno()==null? null : obrigacao.getPathAno().replace("{ANO}", tipo.getAno().toString());
            Files.list(job)
                    .filter(filter->
                            addYear==null ?
                                    Files.isDirectory(filter)
                                    : Files.isDirectory(filter) && filter.getFileName().toString().equals(addYear)
                    )
                    .forEach(f -> {
                        if (obrigacao.getConfig().contains(Month.class)) {
                            String ano = f.getFileName().toString();
                            //pegar o nome do diretorio e remover qualquer outro adicional do nome para pegar o periodo
                            ano = ano.replace(obrigacao.getPathAno().replace("{ANO}",""),"");

                            String estrutura = obrigacao.getEstrutura()+"/"+ano;

                            String addMes = tipo.getMes()==null?
                                    null
                                    :
                                    obrigacao.getPathMes().replace("{MES}", DateUtils.mesString(tipo.getMes().getValue()));


                            try {
                                Files.list(f)
                                        .filter(filter->
                                                addMes==null ?
                                                        Files.isDirectory(filter)
                                                        : Files.isDirectory(filter) && filter.getFileName().toString().equals(addMes)
                                        )
                                        .forEach(t->{

                                        });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }

        return ResponseEntity.noContent().build();
    }

    private void validar(Tipo tipo) throws ParametroNotFoundException, PathInvalidException {
        Tipo.Obrigacao ob = tipo.getObrigacao();
        if(!tipo.getDirForJob().endsWith(ob.getDescricao())) {
            throw new PathInvalidException("O diretorio informado é invalido");
        }
        if(tipo.getAno()==null && ob.getConfig().contains(Year.class)) {
            throw new ParametroNotFoundException("O parametro ano é obrigatório para essa obrigação");
        }
        if(tipo.getMes()==null && ob.getConfig().contains(Month.class)) {
            throw new ParametroNotFoundException("O parametro mês é obrigatório para essa obrigação");
        }
    }
}
