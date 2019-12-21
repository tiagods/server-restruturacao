package com.tiagods.prolink.repository;

import com.tiagods.prolink.dto.ClienteDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends MongoRepository<ClienteDTO,String> {
    Optional<ClienteDTO> findByApelido(Long apelido);
    List<ClienteDTO> findByFolderCreateFalse();
}
