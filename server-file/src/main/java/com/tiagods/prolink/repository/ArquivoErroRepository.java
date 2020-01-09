package com.tiagods.prolink.repository;

import com.tiagods.prolink.dto.ArquivoErroDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArquivoErroRepository extends MongoRepository<ArquivoErroDTO,String> {
}
