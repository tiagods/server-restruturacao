package com.tiagods.prolink.dto;

import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Document(collection = "arquivo_erro")
public class ArquivoErroDTO {
    @Id
    private String id;
    private String cliente;
    private String origem;
    private String destino;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date data;
    private String cause;
    private Status status = Status.ERROR;

    @Getter
    public enum Status {
        WARN("COM SUSPEITA DE NOME DE OUTRO CLIENTE"), ERROR("GERAL");

        private String descricao;
        Status(String descricao){
            this.descricao = descricao;
        }

    }
}
