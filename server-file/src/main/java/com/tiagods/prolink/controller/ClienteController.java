package com.tiagods.prolink.controller;

import com.tiagods.prolink.dto.ClienteDTO;
import com.tiagods.prolink.exception.ClienteNotFoundException;
import com.tiagods.prolink.exception.EstruturaNotFoundException;
import com.tiagods.prolink.service.ClienteDAOService;
import com.tiagods.prolink.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired private ClienteService clienteService;
    @Autowired private ClienteDAOService daoService;

    @GetMapping("/{apelido}/path")
    //@ApiResponse(code = 404 , message = "A pasta do cliente solicitado nao existe" )
    public ResponseEntity<?> getDir(@PathVariable Long apelido){
        Optional<Path> optional = Optional.ofNullable(clienteService.buscarPastaBaseClientePorId(apelido));
        if(optional.isPresent()) return ResponseEntity.ok().body(optional.get().toString());
        else throw new EstruturaNotFoundException("A pasta do cliente solicitado não existe");
    }
    @GetMapping("/{apelido}/organizar")
    public ResponseEntity<?> organizeFoldersClients(@PathVariable Long apelido) throws ClienteNotFoundException, IOException {
        Optional<ClienteDTO> result = daoService.findOne(apelido);
        if (result.isPresent()) {
            clienteService.inicializarPathClientes(result.get(), false, true);
            Optional<Path> optional = Optional.ofNullable(clienteService.buscarPastaBaseClientePorId(apelido));
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
