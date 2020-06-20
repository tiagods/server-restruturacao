package com.tiagods.prolink.repository;

import com.tiagods.prolink.dto.ArquivoDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArquivoRepository extends MongoRepository<ArquivoDTO,String> {
}
