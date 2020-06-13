package com.tiagods.springbootfile.utils;

import com.tiagods.prolink.utils.MyStringUtils;
import org.junit.Assert;
import org.junit.Test;

public class MyStringUtilsTest {

    @Test
    public void removerCaracteresEspeciais(){
        String valor = "12ad2f46546qe8r797!@#$%¨&*";
        String retorno = MyStringUtils.substituirCaracteresEspeciaisPorEspaco(valor, MyStringUtils.TODOS_ECOMECIAL);
        String retorno2 = MyStringUtils.substituirCaracteresEspeciaisPorEspaco(valor, MyStringUtils.TODOS);
        Assert.assertEquals("12ad2f46546qe8r797 & ", retorno);
        Assert.assertEquals("12ad2f46546qe8r797 ", retorno2);
    }

    @Test
    public void normalizarEencurtarNomeDefault(){
        String valor = "LINS & VASCONCELOS PEREIRA";
        String retorno = MyStringUtils.encurtarNome(valor);
        retorno = MyStringUtils.normalizer(retorno);
        Assert.assertEquals("LINS & VASCONCELOS", retorno);
    }

    @Test
    public void novoApelido(){
        Assert.assertEquals("0001", MyStringUtils.novoApelido(1L));
        Assert.assertEquals("0015", MyStringUtils.novoApelido(15L));
        Assert.assertEquals("0501", MyStringUtils.novoApelido(501L));
        Assert.assertEquals("1519", MyStringUtils.novoApelido(1519L));
    }

}
