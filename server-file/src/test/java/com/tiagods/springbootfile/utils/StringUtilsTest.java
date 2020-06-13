package com.tiagods.springbootfile.utils;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void testarBetween() {
        String text = "Julia Evans was born on 25-09-1984. "
                + "She is currently living in the USA (United States of America).";
        String valor = StringUtils.substringBetween(text, "(", ")");
        assert valor == "United States of America";
    }
}
