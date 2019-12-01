package com.tiagods.prolink.service;

import com.tiagods.prolink.model.Arquivo;
import com.tiagods.prolink.repository.ArquivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArquivoService {
    @Autowired
    ArquivoRepository repository;

    public void salvar(Arquivo arquivo){
        repository.save(arquivo);
    }
}
