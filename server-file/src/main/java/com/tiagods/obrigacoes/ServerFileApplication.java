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
        return Arrays.asList(
                "5fbb07352cf9084d5018f859",
                "5fbb07352cf9084d5018f85a",
                "5fbb07362cf9084d5018f85b",
                "5fbb07362cf9084d5018f85c",
                "5fbb07372cf9084d5018f85d",
                "5fbb07372cf9084d5018f85e",
                "5fbb07372cf9084d5018f85f",
                "5fbb07382cf9084d5018f860",
                "5fbb07382cf9084d5018f861",
                "5fbb07392cf9084d5018f862",
                "5fbb07392cf9084d5018f863",
                "5fbb07392cf9084d5018f864",
                "5fbb073a2cf9084d5018f865",
                "5fbb073a2cf9084d5018f866",
                "5fbb073b2cf9084d5018f867",
                "5fbb073b2cf9084d5018f868",
                "5fbb073b2cf9084d5018f869",
                "5fbb073c2cf9084d5018f86a",
                "5fbb073c2cf9084d5018f86b",
                "5fbb073d2cf9084d5018f86c",
                "5fbb073d2cf9084d5018f86d",
                "5fbb073d2cf9084d5018f86e",
                "5fbb073e2cf9084d5018f86f",
                "5fbb073f2cf9084d5018f870",
                "5fbb073f2cf9084d5018f871",
                "5fbb073f2cf9084d5018f872",
                "5fbb07402cf9084d5018f873",
                "5fbb07402cf9084d5018f874",
                "5fbb07412cf9084d5018f875",
                "5fbb07412cf9084d5018f876",
                "5fbb07412cf9084d5018f877",
                "5fbb07422cf9084d5018f878",
                "5fbb07422cf9084d5018f879",
                "5fbb07432cf9084d5018f87a",
                "5fbb07432cf9084d5018f87b",
                "5fbb07432cf9084d5018f87c",
                "5fbb07442cf9084d5018f87d",
                "5fbb07442cf9084d5018f87e",
                "5fbb07442cf9084d5018f87f",
                "5fbb07452cf9084d5018f880",
                "5fbb07452cf9084d5018f881",
                "5fbb07462cf9084d5018f882",
                "5fbb07462cf9084d5018f883",
                "5fbb07462cf9084d5018f884",
                "5fbb07472cf9084d5018f885",
                "5fbb07472cf9084d5018f886",
                "5fbb07482cf9084d5018f887",
                "5fbb07482cf9084d5018f888",
                "5fbb07482cf9084d5018f889",
                "5fbb07492cf9084d5018f88a",
                "5fbb07492cf9084d5018f88b",
                "5fbb074a2cf9084d5018f88c",
                "5fbb074a2cf9084d5018f88d",
                "5fbb074a2cf9084d5018f88e",
                "5fbb074b2cf9084d5018f88f",
                "5fbb074b2cf9084d5018f890",
                "5fbb074c2cf9084d5018f891",
                "5fbb074c2cf9084d5018f892",
                "5fbb074c2cf9084d5018f893",
                "5fbb074d2cf9084d5018f894",
                "5fbb074d2cf9084d5018f895",
                "5fbb074e2cf9084d5018f896",
                "5fbb074e2cf9084d5018f897",
                "5fbb074e2cf9084d5018f898",
                "5fbb074f2cf9084d5018f899",
                "5fbb074f2cf9084d5018f89a",
                "5fbb07502cf9084d5018f89b",
                "5fbb07502cf9084d5018f89c",
                "5fbb07502cf9084d5018f89d",
                "5fbb07512cf9084d5018f89e",
                "5fbb07512cf9084d5018f89f",
                "5fbb07522cf9084d5018f8a0",
                "5fbb07532cf9084d5018f8a1",
                "5fbb07532cf9084d5018f8a2",
                "5fbb07532cf9084d5018f8a3",
                "5fbb07542cf9084d5018f8a4",
                "5fbb07542cf9084d5018f8a5",
                "5fbb07552cf9084d5018f8a6",
                "5fbb07552cf9084d5018f8a7",
                "5fbb07552cf9084d5018f8a8",
                "5fbb07562cf9084d5018f8a9",
                "5fbb07562cf9084d5018f8aa",
                "5fbb07562cf9084d5018f8ab",
                "5fbb07572cf9084d5018f8ac",
                "5fbb07572cf9084d5018f8ad",
                "5fbb07582cf9084d5018f8ae",
                "5fbb07582cf9084d5018f8af",
                "5fbb07592cf9084d5018f8b0",
                "5fbb07592cf9084d5018f8b1",
                "5fbb07592cf9084d5018f8b2",
                "5fbb075a2cf9084d5018f8b3",
                "5fbb075a2cf9084d5018f8b4",
                "5fbb075b2cf9084d5018f8b5",
                "5fbb075b2cf9084d5018f8b6",
                "5fbb075c2cf9084d5018f8b7",
                "5fbb075c2cf9084d5018f8b8",
                "5fbb075c2cf9084d5018f8b9",
                "5fbb075d2cf9084d5018f8ba",
                "5fbb075d2cf9084d5018f8bb",
                "5fbb075e2cf9084d5018f8bc",
                "5fbb075e2cf9084d5018f8bd",
                "5fbb075e2cf9084d5018f8be",
                "5fbb075f2cf9084d5018f8bf",
                "5fbb075f2cf9084d5018f8c0",
                "5fbb07602cf9084d5018f8c1",
                "5fbb07602cf9084d5018f8c2",
                "5fbb07612cf9084d5018f8c3",
                "5fbb07612cf9084d5018f8c4",
                "5fbb07612cf9084d5018f8c5",
                "5fbb07622cf9084d5018f8c6",
                "5fbb07622cf9084d5018f8c7",
                "5fbb07632cf9084d5018f8c8",
                "5fbb07632cf9084d5018f8c9",
                "5fbb07642cf9084d5018f8ca",
                "5fbb07642cf9084d5018f8cb",
                "5fbb07652cf9084d5018f8cc",
                "5fbb07652cf9084d5018f8cd",
                "5fbb07662cf9084d5018f8ce",
                "5fbb07662cf9084d5018f8cf",
                "5fbb07672cf9084d5018f8d0",
                "5fbb07672cf9084d5018f8d1",
                "5fbb07672cf9084d5018f8d2",
                "5fbb07682cf9084d5018f8d3",
                "5fbb07692cf9084d5018f8d4",
                "5fbb07692cf9084d5018f8d5",
                "5fbb076a2cf9084d5018f8d6",
                "5fbb076a2cf9084d5018f8d7",
                "5fbb076b2cf9084d5018f8d8",
                "5fbb076b2cf9084d5018f8d9",
                "5fbb076b2cf9084d5018f8da",
                "5fbb076c2cf9084d5018f8db",
                "5fbb076c2cf9084d5018f8dc",
                "5fbb076d2cf9084d5018f8dd",
                "5fbb076d2cf9084d5018f8de",
                "5fbb076e2cf9084d5018f8df",
                "5fbb076e2cf9084d5018f8e0",
                "5fbb076f2cf9084d5018f8e1",
                "5fbb076f2cf9084d5018f8e2",
                "5fbb07702cf9084d5018f8e3",
                "5fbb07702cf9084d5018f8e4",
                "5fbb07702cf9084d5018f8e5",
                "5fbb07712cf9084d5018f8e6",
                "5fbb07712cf9084d5018f8e7",
                "5fbb07722cf9084d5018f8e8",
                "5fbb07722cf9084d5018f8e9",
                "5fbb07732cf9084d5018f8ea",
                "5fbb07732cf9084d5018f8eb",
                "5fbb07742cf9084d5018f8ec",
                "5fbb07742cf9084d5018f8ed",
                "5fbb07752cf9084d5018f8ee",
                "5fbb07752cf9084d5018f8ef",
                "5fbb07762cf9084d5018f8f0",
                "5fbb07762cf9084d5018f8f1",
                "5fbb07762cf9084d5018f8f2",
                "5fbb07772cf9084d5018f8f3",
                "5fbb07782cf9084d5018f8f4",
                "5fbb07782cf9084d5018f8f5",
                "5fbb07782cf9084d5018f8f6",
                "5fbb07792cf9084d5018f8f7",
                "5fbb07792cf9084d5018f8f8",
                "5fbb077a2cf9084d5018f8f9",
                "5fbb077a2cf9084d5018f8fa",
                "5fbb077b2cf9084d5018f8fb",
                "5fbb077b2cf9084d5018f8fc",
                "5fbb077c2cf9084d5018f8fd",
                "5fbb077c2cf9084d5018f8fe",
                "5fbb077d2cf9084d5018f8ff",
                "5fbb077d2cf9084d5018f900",
                "5fbb077e2cf9084d5018f901",
                "5fbb077e2cf9084d5018f902",
                "5fbb077e2cf9084d5018f903",
                "5fbb077f2cf9084d5018f904",
                "5fbb07802cf9084d5018f905",
                "5fbb07802cf9084d5018f906",
                "5fbb07812cf9084d5018f907",
                "5fbb07812cf9084d5018f908",
                "5fbb07822cf9084d5018f909",
                "5fbb07822cf9084d5018f90a",
                "5fbb07832cf9084d5018f90b",
                "5fbb07832cf9084d5018f90c",
                "5fbb07842cf9084d5018f90d",
                "5fbb07842cf9084d5018f90e",
                "5fbb07842cf9084d5018f90f",
                "5fbb07852cf9084d5018f910",
                "5fbb07862cf9084d5018f911",
                "5fbb07862cf9084d5018f912",
                "5fbb07862cf9084d5018f913",
                "5fbb07872cf9084d5018f914",
                "5fbb07872cf9084d5018f915",
                "5fbb07882cf9084d5018f916",
                "5fbb07882cf9084d5018f917",
                "5fbb07892cf9084d5018f918",
                "5fbb07892cf9084d5018f919",
                "5fbb078a2cf9084d5018f91a",
                "5fbb078a2cf9084d5018f91b",
                "5fbb078b2cf9084d5018f91c",
                "5fbb078b2cf9084d5018f91d",
                "5fbb078c2cf9084d5018f91e",
                "5fbb078c2cf9084d5018f91f",
                "5fbb078c2cf9084d5018f920",
                "5fbb078d2cf9084d5018f921",
                "5fbb078e2cf9084d5018f922",
                "5fbb078e2cf9084d5018f923",
                "5fbb078e2cf9084d5018f924",
                "5fbb078f2cf9084d5018f925",
                "5fbb078f2cf9084d5018f926",
                "5fbb07902cf9084d5018f927",
                "5fbb07902cf9084d5018f928",
                "5fbb07912cf9084d5018f929"
                );
    }
}
