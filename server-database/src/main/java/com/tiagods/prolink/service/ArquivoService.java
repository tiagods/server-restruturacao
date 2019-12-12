package com.tiagods.prolink.service;

import com.tiagods.prolink.dto.ArquivoDTO;
import com.tiagods.prolink.repository.ArquivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArquivoService {
    @Autowired
    ArquivoRepository repository;

    public void salvar(ArquivoDTO arquivo){
        repository.save(arquivo);
    }
}
