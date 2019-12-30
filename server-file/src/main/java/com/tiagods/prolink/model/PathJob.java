package com.tiagods.prolink.model;

import com.tiagods.prolink.validation.PathLocation;
import com.tiagods.prolink.validation.Structure;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class PathJob {
    @NotNull
    @Structure
    private String structure;
    @PathLocation
    private String dirForJob;
}
