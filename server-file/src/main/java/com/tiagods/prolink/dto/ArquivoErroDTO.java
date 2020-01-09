package com.tiagods.prolink.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Document(collection = "arquivo_erro")
public class ArquivoErroDTO {
    @Id
    private String id;
    private String origem;
    private String destino;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date data;
    private String cause;
}
