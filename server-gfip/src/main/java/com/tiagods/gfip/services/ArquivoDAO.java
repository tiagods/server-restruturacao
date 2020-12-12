package com.tiagods.gfip.services;

import com.tiagods.gfip.model.Arquivo;
import com.tiagods.gfip.repository.ArquivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ArquivoDAO {

    @Autowired
    ArquivoRepository arquivoRepository;

    public Arquivo salvar(Arquivo arquivo) {
        return arquivoRepository.save(arquivo);
    }

    public Optional<Arquivo> findById(String id) {
        return arquivoRepository.findById(id);
    }
}

