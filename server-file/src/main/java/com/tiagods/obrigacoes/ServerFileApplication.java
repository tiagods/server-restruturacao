package com.tiagods.obrigacoes;

import com.tiagods.obrigacoes.config.ObrigacaoConfig;
import com.tiagods.obrigacoes.dto.ArquivoDTO;
import com.tiagods.obrigacoes.repository.ArquivoRepository;
import com.tiagods.obrigacoes.schedules.ObrigacoesJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

//@ServletComponentScan
@SpringBootApplication
@EnableScheduling
@Slf4j
public class ServerFileApplication extends SpringBootServletInitializer implements CommandLineRunner {

    @Autowired
    ObrigacaoConfig obrigacaoConfig;

    @Autowired
    ArquivoRepository arquivoRepository;

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
        log.info("Listando variaveis de obrigacoes");
        obrigacaoConfig.getObrigacoes().forEach((key, value)->{
            log.info("Variavel=({}), Path=({})", key, value);
        });
        //job.executar();
    }

    void recuva() {
        List<String> list = getList();
        Iterable<ArquivoDTO> result = arquivoRepository.findAllById(list);
        result.forEach(c->{
            Path destino = Paths.get(c.getDestino());
            if(StringUtils.hasText(c.getDestino()) && Files.exists(destino)) {
                Path novoDestino = Paths.get("\\\\plkserver\\Fiscal\\OBRIGAÇÕES FISCAIS\\DCTF\\2020\\09");
                if(!destino.getFileName().toString().toLowerCase().endsWith(".pdf")){
                    novoDestino = novoDestino.resolve(Paths.get("ARQUIVOS"));
                }
                try {
                    if(!Files.exists(novoDestino)) Files.createDirectories(novoDestino);
                    novoDestino = novoDestino.resolve(destino.getFileName());
                    Files.copy(destino, novoDestino, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Undefined file: " + c);
            }
        });
    }

    static List<String> getList() {
        return Arrays.asList("5fbb07912cf9084d5018f929");
    }
}
