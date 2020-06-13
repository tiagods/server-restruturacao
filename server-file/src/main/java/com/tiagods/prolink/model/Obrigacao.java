package com.tiagods.prolink.model;

import com.tiagods.prolink.validation.PathLocation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.Month;
import java.time.Year;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Obrigacao {
    @NotNull
    private Obrigacao.Tipo tipo;
    private Year ano;
    private Month mes;
    private int cliente;
    @PathLocation
    private String dirForJob;

    @Getter
    public enum Tipo {
        PROLINKDIGITAL
    }
}
