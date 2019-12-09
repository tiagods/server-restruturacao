package com.prolink;

import com.prolink.config.ClienteData;
import com.prolink.model.Cliente;

import java.util.stream.Collectors;

public class StartProject {
    public static void main(String[] args){
        ClienteData data = ClienteData.getInstance();
        String nome = data.getClientes()
                .stream()
                .map(Cliente::toString)
                .collect(Collectors.joining("\n"));
    }

}
