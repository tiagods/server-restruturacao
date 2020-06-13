package com.tiagods.prolink.obrigacao;

import com.tiagods.prolink.exception.ObrigacaoNotFoundException;
import com.tiagods.prolink.model.Obrigacao;
import org.springframework.data.util.Pair;

import java.time.Month;
import java.time.Year;
import java.util.Map;

import static com.tiagods.prolink.obrigacao.OrdemNome.IGUAL;
import static com.tiagods.prolink.obrigacao.OrdemNome.MEIO;

public abstract class ObrigacaoFactory {
    public static ObrigacaoContrato get(Obrigacao obrigacao){
        Obrigacao.Tipo tipo = obrigacao.getTipo();
        Year year = obrigacao.getAno();
        Month month = obrigacao.getMes();

        if(tipo.equals(Obrigacao.Tipo.PROLINKDIGITAL)){
            Pair<String, String> pairMes = Pair.of("PROLINK DIGITAL ","-{ANO}");
            return new ObrigacaoContratoImpl(Map.of(Periodo.ANO, IGUAL, Periodo.MES, MEIO),
                    Pair.of("", ""), pairMes, year, month);
        }
        else if(tipo.equals(Obrigacao.Tipo.IRPF)){
            return new ObrigacaoContratoImpl(Map.of(Periodo.ANO, IGUAL),
                    Pair.of("", ""), Pair.of("",""), year, month);
        }
        throw new ObrigacaoNotFoundException("Recurso nao implementado para esse tipo de obrigação");
    }
}
