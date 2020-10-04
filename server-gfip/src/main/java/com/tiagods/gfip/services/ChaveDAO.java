package com.tiagods.gfip.services;

import com.tiagods.gfip.model.Chave;
import com.tiagods.gfip.repository.ChaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChaveDAO {
    @Autowired
    ChaveRepository chaveRepository;


    void salvar(List<Chave> chaves){
        chaves.forEach(chave -> {
            Chave ch = chave;
            Optional<Chave> result = chaveRepository
                    .findByPathAndCnpj(chave.getPath(), chave.getCnpj());
            if (result.isPresent()) {
                result.get().setPaginas(chave.getPaginas());
                ch = result.get();
            }
            chaveRepository.save(ch);
        });
    }
}
