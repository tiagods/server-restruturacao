package com.tiagods.prolink.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilsValidator {

    public static void main(String[] args) {
        String regex = "(^\\d{2}\\d{3}\\d{3}\\d{4}\\d{2}$)";
        String cnpj = "01.000.394/0001-91";
        System.out.println(cnpjIsValid(cnpj));
        System.out.println(cnpj.matches(regex));
    }

    public static boolean cnpjIsValid(String cnpj){
        String cnpjFormat = "(^\\d{2}\\d{3}\\d{3}\\d{4}\\d{2}$)";
        Matcher matcher = Pattern.compile(cnpjFormat).matcher(cnpj);
        return matcher.find();
    }
}
