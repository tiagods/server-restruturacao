package com.tiagods.prolink.obrigacao;

import com.tiagods.prolink.exception.ParametroIncorretoException;
import com.tiagods.prolink.exception.ParametroNotFoundException;
import com.tiagods.prolink.model.Obrigacao;
import com.tiagods.prolink.utils.DateUtils;
import com.tiagods.prolink.utils.UtilsValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Pair;

import java.time.Month;
import java.time.Year;
import java.util.Map;

@Slf4j
public class ObrigacaoContratoImpl implements ObrigacaoContrato {
    
    private Map<Periodo,OrdemNome>  periodos;
    private PeriodoSubstring substring;
    private Obrigacao obrigacao;
    private Pair<String,String> pairAnoOficial;
    private Pair<String,String> pairMesOficial;

    public ObrigacaoContratoImpl(Obrigacao obrigacao, Map<Periodo,OrdemNome> periodos,
                                 Pair<String,String> pairAno, Pair<String,String> pairMes,
                                 Year ano, Month mes) {
        this.periodos = periodos;
        this.obrigacao = obrigacao;
        this.pairAnoOficial = pairAno;
        this.pairMesOficial = pairMes;
        process(pairAno, pairMes, ano, mes);
    }

    //replace variaveis {ANO}, {MES}
    private void process(Pair<String,String> pairAno, Pair<String,String> pairMes, Year ano, Month mes) {
        Pair<String, String> pairNovoAno = tratar(pairAno, ano, mes);
        Pair<String, String> pairNovoMes = tratar(pairMes, ano, mes);
        this.substring = new PeriodoSubstring(pairNovoAno, pairNovoMes);
        log.info("Implementando obrigacao=["+substring+"]");
    }

    private Pair<String, String> tratar(Pair<String, String> pair, Year ano, Month mes) {
        String primeiro = pair.getFirst();
        String segundo = pair.getSecond();
        String anoString = "";
        String mesString = "";
        if(ano!=null) {
            anoString = DateUtils.anoString(ano);
        }
        else if(mes!=null) {
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
    public String getPastaNome(Periodo periodo, Year year, Month month) {
        StringBuilder builder = new StringBuilder();
        if(periodo.equals(Periodo.MES)) {
            if(periodos.get(Periodo.MES).equals(OrdemNome.MEIO)) {
                builder.append(pairMesOficial.getFirst())
                        .append(DateUtils.mesString(month))
                        .append(pairMesOficial.getSecond());
            }
            else if (periodos.get(Periodo.MES).equals(OrdemNome.IGUAL)) {
                builder.append(DateUtils.mesString(month));
            }
        } else if(periodo.equals(Periodo.ANO)) {
            if(periodos.get(Periodo.ANO).equals(OrdemNome.MEIO)) {
                builder.append(pairAnoOficial.getFirst())
                        .append(pairAnoOficial.getSecond());
            }
            else if (periodos.get(Periodo.ANO).equals(OrdemNome.IGUAL)) {
                builder.append(DateUtils.anoString(year));
            }
        }
        String s = builder.toString().replace("{ANO}", year == null? "" : DateUtils.anoString(year))
                .replace("{MES}", month == null ? "" : DateUtils.mesString(month));
        return s;
    }

    @Override
    public String getMesOuAno(Periodo periodo, String nomePasta) throws ParametroNotFoundException, ParametroIncorretoException {
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
                String message = "O nome da pasta esta inconsistente=[Periodo="+periodo+"]"+"[Pasta="+nomePasta+"]";
                log.error(message);
                throw new ParametroIncorretoException(message);
            }
        }
        else {
            String message = "Periodo invalido para a obrigação=[Periodo="+periodo+"]"+"[Pasta="+nomePasta+"]";
            log.error(message);
            throw new ParametroNotFoundException(message);
        }
    }

    @Override
    public String getEstrutura() {
        return obrigacao.getTipo().getEstrutura();
    }
}
