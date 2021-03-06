package com.tiagods.obrigacoes.repository;

import com.tiagods.obrigacoes.dto.UsuarioDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends MongoRepository<UsuarioDTO, String> {
    Optional<UsuarioDTO> findByEmailAndAtivo(String email, boolean ativo);

    Optional<UsuarioDTO> findByPerfil(UsuarioDTO.Perfil perfil);

    Optional<UsuarioDTO> findByEmail(String email);
}
