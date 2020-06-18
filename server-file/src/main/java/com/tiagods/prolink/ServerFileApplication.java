package com.tiagods.prolink;

import com.tiagods.prolink.config.Regex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
public class ServerFileApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerFileApplication.class,args);
    }
}
