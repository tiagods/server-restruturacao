package com.tiagods.prolink.utils;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class DateUtils {

    public static String anoString(Year ano){
        return ano.toString();
    }

    public static String mesString(Month month){
        return mesString(month.getValue());
    }

    public static String anoString(int ano){
        Year year = Year.of(ano);
        return year.toString();
    }

    public static String mesString(int mes) {
        Month month = Month.of(mes);
        String novo = String.valueOf(month.getValue());
        if(novo.length()==1) novo = "0"+novo;
        return novo;
    }

    public static Set<LocalDate> gerarPeriodosProcessamento (LocalDate data) {
        LocalDate date = (data == null) ? LocalDate.now() : data;
        Set<LocalDate> list = new TreeSet<>();
        //vai processar os 12 ultimos meses, pegando de 2 meses para tras.
        //ex: se estamos em novembro, ira processar agosto e para tras,nunca setembro
        for(int i = -3; i > -15; i--) {
            list.add(date.plusMonths(i));
        }
        return list;
    }
}
