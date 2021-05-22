package com.tiagods.clientesender.service;

import com.tiagods.clientesender.model.NotificacaoControle;
import com.tiagods.clientesender.repository.NotificacaoControleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class NotificacaoControleService {

    @Autowired NotificacaoControleRepository notificacaoControleRepository;

    public Optional<NotificacaoControle> buscaControle(Long idCliente, String processo) {
        return notificacaoControleRepository.findByClienteIdAndProcesso(idCliente, processo);
    }

    public NotificacaoControle salvar(NotificacaoControle controle) {
        return notificacaoControleRepository.save(controle);
    }
}
