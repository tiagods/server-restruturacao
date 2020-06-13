package com.tiagods.prolink.utils;

import java.time.Month;
import java.time.Year;

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

    public static String mesString(int mes){
        Month month = Month.of(mes);
        String novo = String.valueOf(month.getValue());
        if(novo.length()==1) novo = "0"+novo;
        return novo;
    }
}
