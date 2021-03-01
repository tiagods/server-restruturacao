package com.tiagods.obrigacoes.obrigacao;

import com.tiagods.obrigacoes.exception.ObrigacaoNotFoundException;
import com.tiagods.obrigacoes.model.Obrigacao;
import org.springframework.data.util.Pair;

import java.time.Month;
import java.time.Year;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.tiagods.obrigacoes.model.Obrigacao.Tipo.*;
import static com.tiagods.obrigacoes.obrigacao.OrdemNome.IGUAL;
import static com.tiagods.obrigacoes.obrigacao.OrdemNome.MEIO;

public abstract class ObrigacaoFactory {
    public static ObrigacaoContrato get(Obrigacao obrigacao){
        Obrigacao.Tipo tipo = obrigacao.getTipo();
        Year year = obrigacao.getAno();
        Month month = obrigacao.getMes();
        List<Obrigacao.Tipo> obrigacoesMensais = getObrigacoesMensais();
        List<Obrigacao.Tipo> obrigacoesAnuais = getObrigacoesAnuais();

        if(tipo.equals(Obrigacao.Tipo.PROLINKDIGITAL)) {
            Pair<String, String> pairMes = Pair.of("PROLINK DIGITAL ","-{ANO}");
            return new ObrigacaoContratoImpl(obrigacao, Map.of(Periodo.ANO, IGUAL, Periodo.MES, MEIO),
                    Pair.of("", ""), pairMes, year, month);
        } else if(obrigacoesAnuais.contains(tipo)) {
            return new ObrigacaoContratoImpl(obrigacao, Map.of(Periodo.ANO, IGUAL),
                    Pair.of("", ""), Pair.of("",""), year, month);
        } else if(obrigacoesMensais.contains(tipo)) {
            return new ObrigacaoContratoImpl(obrigacao, Map.of(Periodo.ANO, IGUAL, Periodo.MES, IGUAL),
                    Pair.of("", ""), Pair.of("",""), year, month);
        }
        throw new ObrigacaoNotFoundException("Recurso nao implementado para esse tipo de obrigação");
    }
    private static List<Obrigacao.Tipo> getObrigacoesAnuais() {
        return Arrays.asList(IRPF, DIRF);
    }

    private static List<Obrigacao.Tipo> getObrigacoesMensais(){
        return Arrays.asList(DCTF,GIAICMS,REINF,SEDIFDESTDA,SIMPLESNACIONALPGDASD,SINTEGRA,SPEDICMSIPI,SPEDPISCOFINS);
    }
}
