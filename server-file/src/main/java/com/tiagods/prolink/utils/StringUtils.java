package com.tiagods.prolink.utils;

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public final static String CNPJNUMERICO = "[0-9]{14}";
    public final static String TODOS_ECOMECIAL = "\"!@#$%¨*()_+{}|<>:?'-=[]\\,.;/£¢¬§ªº°";//todos exceto e comercial
    public final static String TODOS = "\"!@#$%¨&*()_+{}|<>:?'-=[]\\,.;/£¢¬§ªº°";

    public static String substituirCaracteresEspeciaisPorEspaco(String valor, String regex){
        String newValor = valor;
        for(char b : regex.toCharArray()) {
            //verificar espacos extras criados e substituir
            newValor = newValor.replace(String.valueOf(b), " ");//substituir por espaco
            newValor = newValor.replace("  "," ");
        }
        return newValor;
    }

    public static String novoApelido(Long id){
        int size = String.valueOf(id).length();
        return size==1?"000"+id:
                (size==2?"00"+id:
                        (size==3?"0"+id:String.valueOf(id))
                );
    }

    public static String encurtarNome(String nome) {
        String novoNome = substituirCaracteresEspeciaisPorEspaco(nome, TODOS_ECOMECIAL);
        String[] valor = novoNome.split(" ");
        StringBuilder v1 = new StringBuilder();
        int limite = 20;
        for(int i=0; i<valor.length;i++){
            int size = v1.length();
            if(size+valor[i].length()>=limite) break;
            if(contains(valor[i]) && valor[i+1]!=null){
                if(!contains(valor[i+1]))
                    v1.append(
                            (size+valor[i].length()+valor[i+1].length()<limite)
                                    ? valor[i]+" "+valor[i+1]+" ": "");
            }
            else v1.append(
                    (size+valor[i].length()<limite)
                            ? valor[i]+" ": "");
        }
        return v1.toString().trim();
    }

    private static boolean contains(String value){
        String[] array = new String[]{"DE","EM","A","E"};
        for(String s : array){
            return value.trim().equals(s);
        }
        return false;
    }

    public static String normalizer(String nome){
        String novo = Normalizer.normalize(nome, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
        return novo;
    }

    public static String cnpjNumerico(String cnpj){
        String novo = cnpj;
        for(char b : TODOS_ECOMECIAL.toCharArray()) {
            novo = novo.replace(String.valueOf(b), "");
        }
        return novo;
    }

    public static boolean validarCnpj(String cnpj){
        return Pattern.compile(CNPJNUMERICO).matcher(cnpj).find();
    }
}
