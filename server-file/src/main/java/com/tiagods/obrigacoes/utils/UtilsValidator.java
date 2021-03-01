package com.tiagods.obrigacoes.utils;

public class UtilsValidator {

    private static final String REGEX_CNPJ = "(^\\d{2}\\.\\d{3}\\.\\d{3}\\/\\d{4}\\-\\d{2}$)";
    public final static String CNPJNUMERICO = "[0-9]{14}";
    private static final String REGEX_ANO = "[0-9]{4}";
    private static final String REGEX_MES = "[0-9]{2}";

    public static boolean validarCnpj(String cnpj){
        return cnpj.matches(REGEX_CNPJ);
        //return Pattern.compile(REGEX_CNPJ).matcher(cnpj).find();
    }

    public static boolean validarCnpjNumerico(String cnpj){
        return cnpj.matches(CNPJNUMERICO);
        //return Pattern.compile(CNPJNUMERICO).matcher(cnpj).find();
    }

    public static boolean validarAno(String ano){
        return ano.matches(REGEX_ANO);
    }

    public static boolean validarMes(String mes) {
        if(mes.matches(REGEX_MES)) {
            Integer in = Integer.parseInt(mes);
            return (in > 0 && in <= 12);
        }
        else return false;
    }
}
