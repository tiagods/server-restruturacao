package com.tiagods.clientesender.service;

import com.tiagods.clientesender.model.ClienteResource;
import com.tiagods.clientesender.exception.NotificacaoEnviadaException;
import com.tiagods.clientesender.model.NotificacaoEvento;
import com.tiagods.clientesender.repository.NotificacaoEventoRepository;
import io.reactivex.rxjava3.core.Observable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificacaoEventoService {
    @Autowired
    private NotificacaoEventoRepository repository;

    public Observable<ClienteResource> buscarNotificacao(ClienteResource clienteResource) {
        return Observable.just(clienteResource)
                .flatMap(c-> {
                    var evento = repository.findByClienteIdAndProcesso(clienteResource.getCliente().getIdCliente(), c.getProcesso());
                    if(evento.isPresent()) {
                        return Observable.error(new NotificacaoEnviadaException("Notificacao ja enviada"));
                    }
                    return Observable.just(c);
                });
    }

    public Optional buscarNotificacao(Long cliente, String nomeProcesso) {
        return repository.findByClienteIdAndProcesso(cliente, nomeProcesso);
    }

    public void salvar(NotificacaoEvento notificacao) {
        repository.save(notificacao);
    }
}
