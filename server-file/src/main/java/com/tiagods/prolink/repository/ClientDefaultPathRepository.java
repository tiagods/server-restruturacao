package com.tiagods.prolink.repository;

import com.tiagods.prolink.dto.ClientDefaultPathDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientDefaultPathRepository extends MongoRepository<ClientDefaultPathDTO,String> {
}