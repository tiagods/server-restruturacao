package com.tiagods.clientesender.repository;

import com.tiagods.clientesender.model.NotificacaoControle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface NotificacaoControleRepository extends JpaRepository<NotificacaoControle, Long> {

    Optional<NotificacaoControle> findByClienteIdAndProcesso(Long idCliente, String processo);
}
