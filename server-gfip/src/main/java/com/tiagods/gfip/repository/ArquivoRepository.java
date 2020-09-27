package com.tiagods.gfip.repository;

import com.tiagods.gfip.model.Arquivo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArquivoRepository extends MongoRepository<Arquivo, String> {}
