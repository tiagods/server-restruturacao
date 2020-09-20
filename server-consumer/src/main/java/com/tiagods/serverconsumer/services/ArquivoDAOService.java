package com.tiagods.serverconsumer.services;

import com.tiagods.serverconsumer.dto.ArquivoDTO;
import com.tiagods.serverconsumer.repository.ArquivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArquivoDAOService {
    @Autowired private ArquivoRepository arquivoRepository;

    public ArquivoDTO save(ArquivoDTO arquivo){
        return arquivoRepository.save(arquivo);
    }

}
