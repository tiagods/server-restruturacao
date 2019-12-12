package com.tiagods.prolink.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class ArquivoDTO implements Serializable {
    @Id
    private String id;
    private String nome;
    private String origem;
    private String novoNome;
    private String destino;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date data = new Date();
}
