package com.tiagods.prolink;

import com.tiagods.prolink.model.Arquivo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArquivoRepository extends MongoRepository<Arquivo,String> {
}
