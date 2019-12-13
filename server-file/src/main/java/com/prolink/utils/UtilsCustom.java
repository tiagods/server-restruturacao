package com.prolink.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UtilsCustom {


    public boolean cnpjIsValid(String cnpj){
        String cnpjFormat = "(^\\d{2}\\d{3}\\d{3}\\d{4}\\d{2}$)";
        Matcher matcher = Pattern.compile(cnpjFormat).matcher(cnpj);
        return matcher.find();
    }
}
