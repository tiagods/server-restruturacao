package com.tiagods.prolink.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
public class Arquivo implements Serializable {
    @Id
    private String id;
    private String nome;
    private String origem;
    private String novoNome;
    private String destino;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date data;
}
