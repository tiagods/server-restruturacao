package com.tiagods.toolgfip.repository;

import com.tiagods.toolgfip.model.ChaveGfip;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChaveGfipRepository extends MongoRepository<ChaveGfip, String> {
    List<ChaveGfip> findAllByCnpj(String cnpj);
    List<ChaveGfip> findAllByPath(String path);
    List<ChaveGfip> findAllByParent(String parent);
}
