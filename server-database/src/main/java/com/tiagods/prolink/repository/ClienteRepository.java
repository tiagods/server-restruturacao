package com.tiagods.prolink.repository;

import com.tiagods.prolink.model.Cliente;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends MongoRepository<Cliente,String> {
    Optional<Cliente> findByApelido(String apelido);
    List<Cliente> findByFolderCreateTrue();
}
