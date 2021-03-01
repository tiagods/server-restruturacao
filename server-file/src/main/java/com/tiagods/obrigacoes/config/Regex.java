package com.tiagods.obrigacoes.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "regex")
@Data
public class Regex {
    private String initById;
    private String extractId;
    private String initByIdReplaceNickName;
    private String initByCnpj;
    private String extractCnpj;
    private String structure;
}
