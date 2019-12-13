package com.prolink;

import com.prolink.service.StructureServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerFileApplication{

    public static void main(String[] args) {
        SpringApplication.run(ServerFileApplication.class,args);
    }
}
