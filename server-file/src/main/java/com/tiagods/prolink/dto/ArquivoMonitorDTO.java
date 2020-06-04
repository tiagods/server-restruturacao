package com.tiagods.prolink.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Document(collection="arquivo_monitor")
public class ArquivoMonitorDTO {
    @Id
    private String id;
    private long tamanho = 0;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date data = new Date();
    @Transient
    private boolean salvar = false;
}