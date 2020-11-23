package com.tiagods.prolink;

import com.tiagods.prolink.config.ObrigacaoConfig;
import com.tiagods.prolink.dto.ArquivoDTO;
import com.tiagods.prolink.schedules.ObrigacoesJob;
import com.tiagods.prolink.service.SqsProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ServerFileApplication extends SpringBootServletInitializer implements CommandLineRunner {

    @Autowired
    ObrigacaoConfig obrigacaoConfig;

    public static void main(String[] args) {
        SpringApplication.run(ServerFileApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ServerFileApplication.class);
    }

    @Autowired
    ObrigacoesJob job;

    @Override
    public void run(String... args) throws Exception {
        obrigacaoConfig.getObrigacoes().forEach((key, value)->{
            System.out.println(key+"\t"+ value);
        });
        job.executar();
    }
}
