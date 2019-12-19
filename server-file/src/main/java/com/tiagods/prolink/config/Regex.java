package com.tiagods.prolink.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("regex")
public class Regex {
    private String initById;

    public void setInitById(String initById) {
        this.initById = initById;
    }

    public String getInitById() {
        return initById;
    }
}
