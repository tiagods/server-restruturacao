package com.tiagods.prolink.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Document(collection = "cliente")
public class Cliente implements Serializable {
    @Id
    private String id;
    private Long apelido;
    private String nome;
    private String status;
    private String cnpj;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date data;
    private boolean folderCreate = false;
}
