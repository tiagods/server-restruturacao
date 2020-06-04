package com.tiagods.prolink.repository;

import com.tiagods.prolink.dto.ArquivoMonitorDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArquivoMonitorRepository extends MongoRepository<ArquivoMonitorDTO,String> {
}
