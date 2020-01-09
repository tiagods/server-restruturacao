package com.tiagods.prolink;

import com.tiagods.prolink.service.ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.task.TaskExecutor;

@SpringBootApplication
public class ServerFileApplication implements CommandLineRunner {

    @Autowired
    ClientService service;

    private Logger log = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        SpringApplication.run(ServerFileApplication.class,args);
    }

    @Override
    public void run(String... args) throws Exception {
    }
}
