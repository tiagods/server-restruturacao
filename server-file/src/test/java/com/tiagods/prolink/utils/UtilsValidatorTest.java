package com.tiagods.prolink.utils;

import org.junit.Assert;
import org.junit.Test;

public class UtilsValidatorTest {

    @Test
    public void testCnpj() {
        String cnpj = "01.000.394/0001-91";
        Assert.assertTrue(UtilsValidator.validarCnpj(cnpj));
        Assert.assertFalse(UtilsValidator.validarCnpj(cnpj+"1"));
    }

    @Test
    public void validarCnpjNumerico(){
        String cnpj = "01.100.511/0001-00";
        String cnpjFim = MyStringUtils.cnpjNumerico(cnpj);
        Assert.assertEquals("01100511000100", cnpjFim);
        Assert.assertTrue(UtilsValidator.validarCnpjNumerico(cnpjFim));
    }

    @Test
    public void validarAno() {
        Assert.assertTrue(UtilsValidator.validarAno("2011"));
        Assert.assertTrue(UtilsValidator.validarAno("1900"));
        Assert.assertTrue(UtilsValidator.validarAno("2030"));
        Assert.assertFalse(UtilsValidator.validarAno(" 2011L"));
    }

    @Test
    public void validarMes() {
        Assert.assertFalse(UtilsValidator.validarMes("00"));
        Assert.assertTrue(UtilsValidator.validarMes("01"));
        Assert.assertTrue(UtilsValidator.validarMes("02"));
        Assert.assertTrue(UtilsValidator.validarMes("11"));
        Assert.assertFalse(UtilsValidator.validarMes("13"));
        Assert.assertFalse(UtilsValidator.validarMes(" 13L"));
    }
}
