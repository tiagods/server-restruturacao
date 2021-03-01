package com.tiagods.obrigacoes.repository;

import com.tiagods.obrigacoes.dto.ArquivoDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArquivoRepository extends MongoRepository<ArquivoDTO,String> {
}
