package com.tiagods.prolink.service;

import com.tiagods.prolink.exception.ClienteNotFoundException;
import com.tiagods.prolink.model.Cliente;
import com.tiagods.prolink.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    @Autowired
    ClienteRepository clienteRepository;

    public List<Cliente> list() {
        return clienteRepository.findAll();
    }

    public List<Cliente> listarNaoMapeados() {
        return clienteRepository.findByFolderCreateTrue();
    }

    public void atualizarMapeados(Long apelido) throws ClienteNotFoundException{
        Optional<Cliente> cli = clienteRepository.findByApelido(apelido);
        if(cli.isPresent()){
            Cliente cliente = cli.get();
            cliente.setFolderCreate(true);
            clienteRepository.save(cliente);
        }
        else throw new ClienteNotFoundException("Não foi encontrado um cliente com esse id");
    }

    public void save(Cliente clienteDTO){
        System.out.println("cliente salvo");
        clienteRepository.save(clienteDTO);
    }
}
