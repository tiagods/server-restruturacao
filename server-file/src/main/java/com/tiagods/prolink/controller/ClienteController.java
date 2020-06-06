package com.tiagods.prolink.controller;

import com.tiagods.prolink.exception.StructureNotFoundException;
import com.tiagods.prolink.service.ClienteIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteIOService ioService;
    @GetMapping("/{nickName}/path")

    //@ApiResponse(code = 404 , message = "A pasta do cliente solicitado não existe" )
    public ResponseEntity<?> getDir(@PathVariable Long nickName){
        Optional<Path> optional = Optional.ofNullable(ioService.searchClientPathBaseById(nickName));
        if(optional.isPresent()) return ResponseEntity.ok().body(optional.get().toString());
        else throw new StructureNotFoundException("A pasta do cliente solicitado não existe");
    }
    @GetMapping("/organizar")
    public ResponseEntity<?> organizeFoldersClients(){
        ioService.inicializarPathClientes(true);
        return ResponseEntity.ok().build();
    }
}
