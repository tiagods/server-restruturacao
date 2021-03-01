package com.tiagods.obrigacoes.utils;

import org.springframework.util.MultiValueMap;

import java.util.Optional;
import java.util.UUID;

public class ContextHeaders {

    public static String getCid(MultiValueMap<String, String> headers){
        Optional<String> correlation = Optional.ofNullable(headers.containsKey("cid")? headers.getFirst("cid"):null);
        if(correlation.isPresent()){
            return correlation.get();
        } else {
            return UUID.randomUUID().toString();
        }
    }
}
