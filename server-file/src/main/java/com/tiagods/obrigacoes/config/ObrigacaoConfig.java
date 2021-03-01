package com.tiagods.obrigacoes.config;

import com.tiagods.obrigacoes.model.Obrigacao;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Component
@EnableConfigurationProperties
@ConfigurationProperties("obrigacaoconfig")
@Data
public class ObrigacaoConfig {
    @NotNull
    Map<Obrigacao.Tipo, String> obrigacoes;
}
