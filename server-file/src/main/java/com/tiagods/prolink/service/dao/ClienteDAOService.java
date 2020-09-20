package com.tiagods.prolink.service.dao;

import com.tiagods.prolink.dto.ClientDefaultPathDTO;
import com.tiagods.prolink.dto.ClienteDTO;
import com.tiagods.prolink.exception.ClienteNotFoundException;
import com.tiagods.prolink.repository.ClientDefaultPathRepository;
import com.tiagods.prolink.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteDAOService {
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private ClientDefaultPathRepository pathRepository;

    public List<ClienteDTO> list() {
        return clienteRepository.findAll();
    }

    public List<ClienteDTO> listarPorPastasNaoCriadas() {
        return clienteRepository.findByFolderCreateFalse();
    }

    public Optional<ClienteDTO> findOne(Long apelido){
        return clienteRepository.findByApelido(apelido);
    }

    public List<ClientDefaultPathDTO> listarPastasPadroes(){
        return pathRepository.findAll();
    }

    public void updateThisCreateIsTrue(Long apelido) throws ClienteNotFoundException {
        Optional<ClienteDTO> cli = clienteRepository.findByApelido(apelido);
        if(cli.isPresent()){
            ClienteDTO client = cli.get();
            client.setFolderCreate(true);
            clienteRepository.save(client);
        }
        else throw new ClienteNotFoundException("Não foi encontrado um cliente com esse id");
    }

    public void save(ClienteDTO clientDTO){
        clienteRepository.save(clientDTO);
    }
}
