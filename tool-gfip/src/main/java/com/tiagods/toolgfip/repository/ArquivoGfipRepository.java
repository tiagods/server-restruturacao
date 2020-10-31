package com.tiagods.toolgfip.repository;

import com.tiagods.toolgfip.model.ArquivoGfip;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArquivoGfipRepository extends MongoRepository<ArquivoGfip, String> {
    Optional<ArquivoGfip> findByDiretorio(String toString);
}
