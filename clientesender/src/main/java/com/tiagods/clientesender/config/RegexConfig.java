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
@ConfigurationProperties("regexconfig")
@Data
public class RegexConfig {
    String initById;
    @NotNull
    Map<ProcessoEnum, String> regex;
}
