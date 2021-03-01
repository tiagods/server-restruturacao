package com.tiagods.obrigacoes.repository;

import com.tiagods.obrigacoes.dto.ArquivoErroDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArquivoErroRepository extends MongoRepository<ArquivoErroDTO,String> {
}
