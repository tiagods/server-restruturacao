package com.tiagods.gfip.resources;

import com.tiagods.gfip.repository.ArquivoRepository;
import com.tiagods.gfip.services.MapearGfip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ArquivosResources {

    @Autowired
    private ArquivoRepository repository;
    @Autowired
    MapearGfip mapearGfip;

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        repository.count();
        String message = "{\"message\":\"A aplicacao esta rodando\"}";
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @Async
    @GetMapping("/mapear-gifp")
    public ResponseEntity<?> iniciarMapeamento() {
        if(isProcessoRodando()){
            String message = "{\"message\":\"Ja existe outro processo executando essa analise\"}";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        mapearGfip.iniciarMapeamento();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verificar-gfip")
    public ResponseEntity<?> verificar() {
        String message = "{\"message\":\"Processo de mapeamento da gfip não esta rodando\"}";
        if(isProcessoRodando()){
             message = "{\"message\":\"Processo de mapeamento da gfip esta em andamento\"}";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        return ResponseEntity.ok().body(message);
    }

    public boolean isProcessoRodando() {
        return mapearGfip.isProcessoRodando();
    }
}
