package com.tiagods.prolink;

import com.tiagods.prolink.config.Regex;
import com.tiagods.prolink.config.ServerFile;
import com.tiagods.prolink.service.ClientIOService;
import com.tiagods.prolink.service.ClienteService;
import com.tiagods.prolink.utils.MoverPastas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ServerFileApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerFileApplication.class,args);
    }

}
