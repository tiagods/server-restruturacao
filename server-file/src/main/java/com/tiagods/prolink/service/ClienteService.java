package com.tiagods.prolink.service;

import com.tiagods.prolink.dto.ClienteDTO;
import com.tiagods.prolink.exception.ClienteNotFoundException;
import com.tiagods.prolink.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    public List<ClienteDTO> list() {
        return clienteRepository.findAll();
    }

    public List<ClienteDTO> listarNaoMapeados() {
        return clienteRepository.findByFolderCreateFalse();
    }

    public void atualizarMapeados(Long apelido) throws ClienteNotFoundException{
        Optional<ClienteDTO> cli = clienteRepository.findByApelido(apelido);
        if(cli.isPresent()){
            ClienteDTO cliente = cli.get();
            cliente.setFolderCreate(true);
            clienteRepository.save(cliente);
        }
        else throw new ClienteNotFoundException("Não foi encontrado um cliente com esse id");
    }

    public void save(ClienteDTO clienteDTO){
        clienteRepository.save(clienteDTO);
    }
}
