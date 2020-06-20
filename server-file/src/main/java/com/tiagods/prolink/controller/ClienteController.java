package com.tiagods.prolink.controller;

import com.tiagods.prolink.dto.ClienteDTO;
import com.tiagods.prolink.exception.ClienteNotFoundException;
import com.tiagods.prolink.exception.EstruturaNotFoundException;
import com.tiagods.prolink.service.ClienteDAOService;
import com.tiagods.prolink.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            clienteService.inicializarPathClientes(result.get(), false);
            Optional<Path> optional = Optional.ofNullable(clienteService.buscarPastaBaseClientePorId(apelido));
            if(optional.isPresent()){
                return ResponseEntity.ok().body(clienteService.listarDiretorios(optional.get()));
            }
            else
                return ResponseEntity.badRequest().build();
        } else {
            throw new ClienteNotFoundException("Não existe um cliente com esse id");
        }
    }
}
