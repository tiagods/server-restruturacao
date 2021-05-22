package com.tiagods.clientesender.config;

import com.tiagods.clientesender.model.ProcessoEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Component
@EnableConfigurationProperties
@ConfigurationProperties("emailconfig")
@Data
public class EmailConfig {
    @NotNull
    Map<ProcessoEnum, String> de;
    @NotNull
    Map<ProcessoEnum, String> cc;
    @NotNull
    Map<ProcessoEnum, String> assunto1;
    @NotNull
    Map<ProcessoEnum, String> assunto2;
    @NotNull
    Map<ProcessoEnum, String> assunto3;
    @NotNull
    Map<ProcessoEnum, String> outrosArquivos;
    @NotNull
    Map<ProcessoEnum, String> template1;
    @NotNull
    Map<ProcessoEnum, String> template2;
    @NotNull
    Map<ProcessoEnum, String> template3;
}
