package com.tiagods.prolink.model;

import com.tiagods.prolink.validation.PathLocation;
import com.tiagods.prolink.validation.Structure;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class PathJob {
    //@Pattern(regexp="[a-zA-Z0-9&_/-]{3,20}",message="Parametro incorreto, nome valido (A a Z), 0 a 9, minimo de 3 caracteres e maximo 20")
    @NotNull
    @Structure
    private String structure;
    @PathLocation
    private String dirForJob;
}
