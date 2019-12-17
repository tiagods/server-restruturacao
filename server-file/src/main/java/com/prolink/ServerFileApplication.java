package com.prolink;

import com.tiagods.prolink.repository.ArquivoRepository;
import com.tiagods.prolink.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:arquivos.yml")

public class ServerFileApplication{

    @Value("${fileServer.base}") private static String sBase;
    @Value("${fileServer.model}") private static String sModel;
    @Value("${fileServer.shutdown}") private static String sShutdown;


    @Autowired
    static ClienteRepository clientes;

    public static void main(String[] args) {
        SpringApplication.run(ServerFileApplication.class,args);
        System.out.println(sBase);
        clientes.findAll().forEach(c->
                System.out.println(c.getApelido()+"-"+c.getStatus()+"-"+c.getNome())
        );
    }
}
