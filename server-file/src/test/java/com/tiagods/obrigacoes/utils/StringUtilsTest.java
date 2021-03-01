package com.tiagods.obrigacoes.utils;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void testarBetween() {
        String text = "Julia Evans was born on 25-09-1984. "
                + "She is currently living in the USA (United States of America).";
        String valor = StringUtils.substringBetween(text, "(", ")");
        Assert.assertEquals(valor, "United States of America");
    }

    @Test
    public void testarBetween2() {
        String text = "Julia Evans was born on 25-09-1984. "
                + "She is currently living in the USA (United States of America).";
        String valor = StringUtils.substringBetween(text, "Julia Evans was born on ", ".");
        Assert.assertEquals(valor, "25-09-1984");
    }

    @Test
    public void testarBetween3() {
        String text = "PROLINK DIGITAL 08-2012";
        String valor = StringUtils.substringBetween(text, "PROLINK DIGITAL ", "-");
        Assert.assertEquals(valor, "08");
    }

}
