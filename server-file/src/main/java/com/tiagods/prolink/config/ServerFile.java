package com.tiagods.prolink.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
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
