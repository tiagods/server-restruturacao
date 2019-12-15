package com.prolink.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UtilsValidator {

    @Value("${regex.initById}") private String initById;

    public static void main(String[] args) {
        String regex = "(^\\d{2}\\d{3}\\d{3}\\d{4}\\d{2}$)";
        String cnpj = "04.110.394/0001-91";
        System.out.println(cnpjIsValid(cnpj));
        System.out.println(cnpj.matches(regex));
    }

    public static boolean cnpjIsValid(String cnpj){
        String cnpjFormat = "(^\\d{2}\\d{3}\\d{3}\\d{4}\\d{2}$)";
        Matcher matcher = Pattern.compile(cnpjFormat).matcher(cnpj);
        return matcher.find();
    }

    public String getInitById() {
        return initById;
    }
}
