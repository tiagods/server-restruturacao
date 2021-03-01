package com.tiagods.obrigacoes.model;

import com.tiagods.obrigacoes.validation.PathLocation;
import com.tiagods.obrigacoes.validation.Structure;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PathJob {
    @NotNull
    @Structure
    private String estrutura;
    @PathLocation
    private String dirForJob;
}
