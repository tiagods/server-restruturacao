package com.prolink.olders.config;

import com.prolink.model.Cliente;

import java.io.IOException;
import java.util.Set;

public abstract class BasePath {

    private BasePath(){
        try {

            ClienteData clienteData = ClienteData.getInstance();
            Set<Cliente> clienteSet = clienteData.getClientes();

            //organizarCliente(clienteSet,files);
            throw new IOException();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

  }
