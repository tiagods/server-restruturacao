package com.tiagods.prolink.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@Document(collection="arquivo")
public class ArquivoDTO implements Serializable {
    @Id
    private String id;
    private String nome;
    private String origem;
    private String novoNome;
    private String destino;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date data;
}
