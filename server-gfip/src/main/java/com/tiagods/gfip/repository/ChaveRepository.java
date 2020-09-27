package com.tiagods.gfip.repository;

import com.tiagods.gfip.model.Chave;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChaveRepository extends MongoRepository<Chave, String> {
}
