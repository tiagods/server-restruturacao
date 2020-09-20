package com.tiagods.serverconsumer.repository;

import com.tiagods.serverconsumer.dto.ArquivoDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArquivoRepository extends MongoRepository<ArquivoDTO,String> {
}
