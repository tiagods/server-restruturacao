package com.prolink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.PropertySource;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
@PropertySource(value = "classpath:location.yml",factory = YamlPropertySourceLoader::class)
public class ServerFileApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerFileApplication.class,args);
        System.out.println("${serverfile.base}");
        System.out.println("${serverfile.desligado}");
        System.out.println("${serverfile.modelo}");
        Path path = Paths.get("${serverfile.desligado}}");

        System.out.println(path.toAbsolutePath());
    }
}
