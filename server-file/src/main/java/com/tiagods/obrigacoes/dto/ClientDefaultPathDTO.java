package com.tiagods.obrigacoes.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "client_path")
public class ClientDefaultPathDTO {
    @Id
    private String id;
    private String value;
}
