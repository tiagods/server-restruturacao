package com.tiagods.prolink.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Getter
@Setter
public class Cliente implements Serializable {
    @Id
    private long id;
    private String apelido;
    private String nome;
    private String status;
    private String cnpj;
    private boolean folderCreate;
}
