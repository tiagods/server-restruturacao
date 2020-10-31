package com.tiagods.toolgfip.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@Document(collection = "cliente")
@AllArgsConstructor
@NoArgsConstructor
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
