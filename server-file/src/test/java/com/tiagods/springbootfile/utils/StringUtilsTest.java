package com.tiagods.springbootfile.utils;

import com.tiagods.prolink.utils.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void removerCaracteresEspeciais(){
        String valor = "12ad2f46546qe8r797!@#$%¨&*";
        String retorno = StringUtils.substituirCaracteresEspeciaisPorEspaco(valor,StringUtils.TODOS_ECOMECIAL);
        String retorno2 = StringUtils.substituirCaracteresEspeciaisPorEspaco(valor,StringUtils.TODOS);
        Assert.assertEquals("12ad2f46546qe8r797 & ", retorno);
        Assert.assertEquals("12ad2f46546qe8r797 ", retorno2);
    }

    @Test
    public void normalizarEencurtarNomeDefault(){
        String valor = "LINS & VASCONCELOS PEREIRA";
        String retorno = StringUtils.encurtarNome(valor);
        retorno = StringUtils.normalizer(retorno);
        Assert.assertEquals("LINS & VASCONCELOS", retorno);
    }

    @Test
    public void validarCnpj(){
        String cnpj = "01.100.511/0001-00";
        String cnpjFim = StringUtils.cnpjNumerico(cnpj);
        Assert.assertEquals("01100511000100", cnpjFim);
        Assert.assertTrue(StringUtils.validarCnpj(cnpjFim));
    }

    @Test
    public void novoApelido(){
        Assert.assertEquals("0001", StringUtils.novoApelido(1L));
        Assert.assertEquals("0015", StringUtils.novoApelido(15L));
        Assert.assertEquals("0501", StringUtils.novoApelido(501L));
        Assert.assertEquals("1519", StringUtils.novoApelido(1519L));
    }

}
