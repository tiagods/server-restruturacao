package com.tiagods.prolink.controller;

import com.tiagods.prolink.exception.StructureNotFoundException;
import com.tiagods.prolink.model.Cliente;
import com.tiagods.prolink.service.ClientIOService;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.Optional;

@RestController
@RequestMapping("/clients")
public class ClienteController {

    @Autowired
    private ClientIOService ioService;
    @GetMapping("/{nickname}/path")
//    @ApiResponse(code = 404 , message = "A pasta do cliente solicitado não existe" )
    public ResponseEntity<?> getDir(@PathVariable Long nickname){
        Optional<Path> optional = Optional.ofNullable(ioService.searchClientPathBaseById(nickname));
        if(optional.isPresent()) return ResponseEntity.ok().body(optional.get().toString());
        else throw new StructureNotFoundException("A pasta do cliente solicitado não existe");
    }
    @GetMapping("/organize")
    public ResponseEntity<?> organizeFoldersClients(){
        ioService.initClientsPaths(true);
        return ResponseEntity.ok().build();
    }
}
