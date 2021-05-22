package com.tiagods.clientesender.repository;

import com.tiagods.clientesender.model.NotificacaoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface NotificacaoEventoRepository extends JpaRepository<NotificacaoEvento, Long> {

    Optional<NotificacaoEvento> findByClienteIdAndProcesso(Long clienteId, String processo);
}
