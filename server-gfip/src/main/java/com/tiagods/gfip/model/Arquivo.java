package com.tiagods.gfip.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="arquivo_gfip")
public class Arquivo implements Serializable {
    @Id
    private String id;
    private String diretorio;
    private String obrigacao;
    private LocalDate periodo;
    @Transient
    private List<Chave> chaves;
}
