package com.tiagods.obrigacoes.controller;

import com.tiagods.obrigacoes.dto.ClienteDTO;
import com.tiagods.obrigacoes.exception.ClienteNotFoundException;
import com.tiagods.obrigacoes.exception.EstruturaNotFoundException;
import com.tiagods.obrigacoes.service.dao.ClienteDAOService;
import com.tiagods.obrigacoes.service.ClienteService;
import com.tiagods.obrigacoes.utils.ContextHeaders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
@Slf4j
public class ClienteController {

    @Autowired private ClienteService clienteService;
    @Autowired private ClienteDAOService daoService;

    @GetMapping("/{apelido}/path")
    //@ApiResponse(code = 404 , message = "A pasta do cliente solicitado nao existe" )
    public ResponseEntity<?> getDir(@RequestHeader MultiValueMap<String, String> headers, @PathVariable @Valid Long apelido){
        String cid = ContextHeaders.getCid(headers);
        log.info("Correlation: [{}] GET api/clientes/{}/path", cid, apelido);
        Optional<Path> optional = Optional.ofNullable(clienteService.buscarPastaBaseClientePorId(cid, apelido));
        if(optional.isPresent()) {
            return ResponseEntity.ok().body(optional.get().toString());
        }
        else {
            throw new EstruturaNotFoundException("A pasta do cliente solicitado não existe");
        }
    }
    @GetMapping("/{apelido}/organizar")
    public ResponseEntity<?> organizeFoldersClients(@RequestHeader MultiValueMap<String, String> headers, @PathVariable @Valid Long apelido) throws ClienteNotFoundException, IOException {
        String cid = ContextHeaders.getCid(headers);
        log.info("Correlation: [{}] GET api/clientes/{}/organizar", cid, apelido);

        Optional<ClienteDTO> result = daoService.findOne(apelido);
        if (result.isPresent()) {
            clienteService.inicializarPathClientes(cid, result.get(), false, true);
            Optional<Path> optional = Optional.ofNullable(clienteService.buscarPastaBaseClientePorId(cid, apelido));
            if(optional.isPresent()){
                return ResponseEntity.ok().body(
                        clienteService.listarDiretorios(optional.get())
                        .stream()
                        .map(Path::toString));
            }
            else
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\":\"Nao existe pastas para esse cliente\"}");
        } else {
            throw new ClienteNotFoundException("Não existe um cliente com esse id");
        }
    }
}
