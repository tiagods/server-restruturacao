package com.tiagods.prolink.resource;

import com.tiagods.prolink.exception.ClienteNotFoundException;
import com.tiagods.prolink.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clientes")
public class ClienteResource {

    @Autowired
    ClienteService clienteService;

    @GetMapping
    public ResponseEntity<?> listar(){
        return ResponseEntity.ok(clienteService.list());
    }
    @GetMapping(value = "/mapping")
    public ResponseEntity<?> mapear(){
        return ResponseEntity.ok(clienteService.listarNaoMapeados());
    }

    @GetMapping(value = "/{apelido}/mapeado")
    public ResponseEntity<?> mapear(@PathVariable("apelido") String apelido) throws ClienteNotFoundException {
        clienteService.atualizarMapeados(apelido);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

}
