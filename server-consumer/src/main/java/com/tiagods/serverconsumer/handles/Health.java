package com.tiagods.serverconsumer.handles;

import com.tiagods.serverconsumer.repository.ArquivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class Health {

    @Autowired
    ArquivoRepository repository;

    @GetMapping
    public ResponseEntity<?> health(){
        repository.count();
        return ResponseEntity.ok().body("{\"message\":\"Application is running\"}");
    }

}
