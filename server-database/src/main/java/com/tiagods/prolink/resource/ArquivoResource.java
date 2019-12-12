package com.tiagods.prolink.resource;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/arquivos")
public class ArquivoResource {

    @GetMapping("/buscarPorPeriodo")
    public ResponseEntity<?> porPeriod(
            @RequestBody @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date de,
            @RequestBody @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss") Date ate
            ){
        Object[] o = new Object[]{de,ate};
        System.out.println(de);
        System.out.println(ate);
        return ResponseEntity.ok().build();
    }
}
