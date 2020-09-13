package com.tiagods.prolink.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Document(collection = "usuario")
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO implements Serializable {
    @Id
    private String id;
    @NotNull @NotBlank
    private String email;
    @NotNull @NotBlank
    private String nome;
    @JsonInclude(Include.NON_NULL)
    @NotNull @NotBlank
    private String senha;
    @JsonInclude(Include.NON_NULL)
    private String tokenAdmin;
    private boolean ativo = true;
    private Perfil perfil = Perfil.GERENTE;

    public enum Perfil {
        ADMIN, GERENTE
    }
}
