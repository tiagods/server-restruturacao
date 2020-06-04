package com.tiagods.prolink.model;

import com.tiagods.prolink.validation.PathLocation;
import com.tiagods.prolink.validation.Structure;
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
    private String structure;
    @PathLocation
    private String dirForJob;
}
