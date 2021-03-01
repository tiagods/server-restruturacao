package com.tiagods.obrigacoes.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
@EnableConfigurationProperties
@ConfigurationProperties("serverfile")
@Data
public class ServerFile {
    @NotNull
    private String root;
    @NotNull
    private String base;
    @NotNull
    private String model;
    @NotNull
    private String shutdown;
}
