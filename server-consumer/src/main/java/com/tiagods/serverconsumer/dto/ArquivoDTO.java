package com.tiagods.serverconsumer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@Document(collection="arquivo")
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class ArquivoDTO implements Serializable {
    @JsonIgnore
    @Id
    private String id;
    private String cliente;
    private String nome;
    private String origem;
    private String novoNome;
    private String destino;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date data;
    private String correlation;
    private String token;
    private String usuario;
}
