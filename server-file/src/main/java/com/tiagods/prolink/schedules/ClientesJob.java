package com.tiagods.prolink.schedules;

import com.tiagods.prolink.service.ClienteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class ClientesJob {

    @Autowired private ClienteService clienteService;

    //realizar as segundas, quartas e sextas
    public void organizarCliente(){
        clienteService.inicializarPathClientes(null,true);
    }
}
