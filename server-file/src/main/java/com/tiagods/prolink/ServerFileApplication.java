package com.tiagods.prolink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ServerFileApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerFileApplication.class,args);
    }
}
