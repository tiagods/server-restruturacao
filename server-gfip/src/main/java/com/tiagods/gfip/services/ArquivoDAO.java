package com.tiagods.gfip.services;

import com.tiagods.gfip.model.Arquivo;
import com.tiagods.gfip.repository.ArquivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArquivoDAO {

    @Autowired
    ArquivoRepository arquivoRepository;

    public Arquivo salvar(Arquivo arquivo) {
        return arquivoRepository.save(arquivo);
    }
}

