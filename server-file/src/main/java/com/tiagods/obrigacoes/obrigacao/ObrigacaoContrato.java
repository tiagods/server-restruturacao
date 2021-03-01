package com.tiagods.obrigacoes.obrigacao;

import com.tiagods.obrigacoes.exception.ParametroIncorretoException;
import com.tiagods.obrigacoes.exception.ParametroNotFoundException;

import java.time.Month;
import java.time.Year;

public interface ObrigacaoContrato {
    boolean contains(Periodo periodo);

    String getPastaNome(Periodo periodo, Year year, Month month);

    String getMesOuAno(Periodo periodo, String nomePasta) throws ParametroNotFoundException, ParametroIncorretoException;

    String getEstrutura();
}
