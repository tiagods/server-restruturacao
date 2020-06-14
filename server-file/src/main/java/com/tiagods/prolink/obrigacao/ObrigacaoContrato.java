package com.tiagods.prolink.obrigacao;

import com.tiagods.prolink.exception.ParametroIncorretoException;
import com.tiagods.prolink.exception.ParametroNotFoundException;

import java.time.Month;
import java.time.Year;

public interface ObrigacaoContrato {
    boolean contains(Periodo periodo);

    String getPastaNome(Periodo periodo, Year year, Month month);

    String getMesOuAno(Periodo periodo, String nomePasta) throws ParametroNotFoundException, ParametroIncorretoException;

    String getEstrutura();
}
