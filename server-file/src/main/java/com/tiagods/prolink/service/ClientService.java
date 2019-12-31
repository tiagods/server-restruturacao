package com.tiagods.prolink.service;

import com.tiagods.prolink.dto.ClientDefaultPathDTO;
import com.tiagods.prolink.dto.ClienteDTO;
import com.tiagods.prolink.exception.ClientNotFoundException;
import com.tiagods.prolink.repository.ClientDefaultPathRepository;
import com.tiagods.prolink.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ClientDefaultPathRepository pathRepository;

    public List<ClienteDTO> list() {
        return clienteRepository.findAll();
    }

    public List<ClienteDTO> findAllByFolderCreateFalse() {
        return clienteRepository.findByFolderCreateFalse();
    }

    public Optional<ClienteDTO> findOne(Long apelido){
        return clienteRepository.findByApelido(apelido);
    }

    public List<ClientDefaultPathDTO> getPathsForClient(){
        return pathRepository.findAll();
    }

    public void updateThisCreateIsTrue(Long apelido) throws ClientNotFoundException {
        Optional<ClienteDTO> cli = clienteRepository.findByApelido(apelido);
        if(cli.isPresent()){
            ClienteDTO client = cli.get();
            client.setFolderCreate(true);
            clienteRepository.save(client);
        }
        else throw new ClientNotFoundException("Não foi encontrado um cliente com esse id");
    }

    public void save(ClienteDTO clientDTO){
        clienteRepository.save(clientDTO);
    }
}
