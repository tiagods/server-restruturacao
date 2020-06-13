package com.tiagods.springbootfile.utils;

import org.junit.Assert;
import org.junit.Test;

import static com.tiagods.prolink.utils.UtilsValidator.cnpjIsValid;

public class UtilsValidatorTest {

    @Test
    public void testCnpj() {
        String cnpj = "01.000.394/0001-91";
        Assert.assertTrue(cnpjIsValid(cnpj));
    }
}
