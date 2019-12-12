package com.tiagods.prolink.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Getter
@Setter
public class ClienteDTO implements Serializable {
    @Id
    private long id;
    private Integer apelido;
    private String nome;
    private String status;
    private String cnpj;
    private boolean folderCreate;
}
