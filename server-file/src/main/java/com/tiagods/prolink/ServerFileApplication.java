package com.tiagods.prolink;

import com.tiagods.prolink.config.Regex;
import com.tiagods.prolink.config.ServerFile;
import com.tiagods.prolink.service.ClienteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
public class ServerFileApplication implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ServerFile serverFile;

    @Autowired
    private Regex regex;

    @Autowired
    private ClienteService clientes;

    public static void main(String[] args) {
        SpringApplication.run(ServerFileApplication.class,args);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Regex:" +regex.getInitById());
        logger.info("ServerFile base: "+serverFile.getBase());
        logger.info("ServerFile model: "+serverFile.getModel());
        logger.info("ServerFile shutdown: "+serverFile.getShutdown());
        clientes.list().forEach(c->
                logger.info(c.getApelido()+"-"+c.getStatus()+"-"+c.getNome())
        );
    }
}
