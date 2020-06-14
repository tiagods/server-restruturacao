package com.tiagods.prolink.obrigacao;

import com.tiagods.prolink.exception.ParametroIncorretoException;
import com.tiagods.prolink.exception.ParametroNotFoundException;

public interface ObrigacaoContrato {
    boolean contains(Periodo periodo);
    String get(Periodo periodo, String nomePasta) throws ParametroNotFoundException, ParametroIncorretoException;
}
