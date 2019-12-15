package com.prolink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerFileApplication{
    public static void main(String[] args) {
        SpringApplication.run(ServerFileApplication.class,args);
        Cliente cliente = new Cliente();
    }
}
