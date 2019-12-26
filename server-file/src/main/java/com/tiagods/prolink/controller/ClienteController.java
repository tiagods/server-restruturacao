package com.tiagods.prolink.controller;

import com.tiagods.prolink.model.Cliente;
import com.tiagods.prolink.service.ClientIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClientIOService ioService;

    @GetMapping("/{apelido}/diretorio")
    public ResponseEntity<?> pegarDiretorio(@PathVariable Long apelido){
        return ResponseEntity.status(HttpStatus.OK).body(ioService.searchClientPathBaseById(apelido).toString());
    }
}
