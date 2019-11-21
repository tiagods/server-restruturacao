package com.prolink.job;

import com.prolink.config.BasePath;
import com.prolink.config.ClienteData;
import com.prolink.model.Cliente;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

public class DiretoriosClientes extends BasePath {
    public DiretoriosClientes(){
        super();
    }
    public static void main(String[] args) {
        new DiretoriosClientes().organizar();
    }
    public void organizar(){
        ClienteData data = ClienteData.getInstance();
        Path base = getBase();

        Set<Cliente> clientes = data.getClientes();
        clientes.forEach(c -> {
            try{
                organizarCliente(c);
            }catch (IOException e){
                e.printStackTrace();
            }
        });
    }
}
