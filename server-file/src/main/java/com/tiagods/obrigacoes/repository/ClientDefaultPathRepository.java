package com.tiagods.obrigacoes.repository;

import com.tiagods.obrigacoes.dto.ClientDefaultPathDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientDefaultPathRepository extends MongoRepository<ClientDefaultPathDTO,String> {
}