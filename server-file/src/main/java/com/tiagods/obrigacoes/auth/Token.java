package com.tiagods.obrigacoes.auth;

import lombok.Getter;
import java.io.Serializable;

@Getter
public class Token implements Serializable {
    private final String token;
    public Token(String token) {
        this.token = token;
    }
}
