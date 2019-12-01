package com.prolink;

import com.prolink.config.ClienteData;
import com.prolink.dao.ClienteDAO;
import com.prolink.model.Cliente;

import java.util.stream.Collectors;

public class StartProject {
    public static void main(String[] args){
        ClienteData data = ClienteData.getInstance();
        ClienteDAO dao = new ClienteDAO();
        data.save(dao.refreshClientes());
        String nome = data.getClientes()
                .stream()
                .map(Cliente::toString)
                .collect(Collectors.joining("\n"));
    }

}
