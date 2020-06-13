package com.tiagods.prolink.obrigacao;

import com.tiagods.prolink.exception.ParametroIncorretoException;
import com.tiagods.prolink.exception.ParametroNotFoundException;
import com.tiagods.prolink.utils.DateUtils;
import com.tiagods.prolink.utils.UtilsValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Pair;

import java.time.Month;
import java.time.Year;
import java.util.Map;

public class ObrigacaoContratoImpl implements ObrigacaoContrato {
    
    private Map<Periodo, OrdemNome>  periodos;
    private PeriodoSubstring substring;

    public ObrigacaoContratoImpl(Map<Periodo, OrdemNome> periodos,
                                 Pair<String, String> pairAno, Pair<String, String> pairMes,
                                 Year ano, Month mes) {
        this.periodos = periodos;
        process(pairAno, pairMes, ano, mes);
    }

    //replace variaveis {ANO}, {MES}
    private void process(Pair<String, String> pairAno, Pair<String, String> pairMes, Year ano, Month mes) {
        Pair<String, String> pairNovoAno = tratar(pairAno, ano, mes);
        Pair<String, String> pairNovoMes = tratar(pairMes, ano, mes);
        this.substring = new PeriodoSubstring(pairNovoAno, pairNovoMes);
    }

    private Pair<String, String> tratar(Pair<String, String> pair, Year ano, Month mes) {
        String primeiro = pair.getFirst();
        String segundo = pair.getSecond();

        String anoString = "";
        String mesString = "";
        if(ano!=null) {
            anoString = DateUtils.anoString(ano);
        }
        if(mes!=null) {
            mesString = DateUtils.mesString(mes);
        }
        primeiro = primeiro.replace("{ANO}", anoString).replace("{MES}", mesString);
        segundo = segundo.replace("{ANO}", anoString).replace("{MES}", mesString);
        return Pair.of(primeiro, segundo);
    }

    @Override
    public boolean contains(Periodo object){
        return periodos.containsKey(object);
    }

    @Override
    public String get(Periodo periodo, String nomePasta) throws ParametroNotFoundException, ParametroIncorretoException {
        if(contains(periodo)) {
            OrdemNome ordem = periodos.get(periodo);
            Pair<String, String> pair = substring.getPair(periodo);
            String valor = "";
            if(ordem.equals(OrdemNome.MEIO)){
                valor = StringUtils.substringBetween(nomePasta, pair.getFirst(), pair.getSecond());
            } else if (ordem.equals(OrdemNome.IGUAL)) {
                valor = nomePasta;
            }

            if(periodo.equals(Periodo.ANO) && UtilsValidator.validarAno(valor)
                    || periodo.equals(Periodo.MES) && UtilsValidator.validarMes(valor)
            ) {
                return valor;
            } else {
                throw new ParametroIncorretoException("O nome da pasta esta inconsistente");
            }

        }
        throw new ParametroNotFoundException("Periodo invalido para a obrigação");
    }

}
