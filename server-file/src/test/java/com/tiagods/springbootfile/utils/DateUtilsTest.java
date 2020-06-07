package com.tiagods.springbootfile.utils;

import com.tiagods.prolink.utils.DateUtils;
import org.junit.Assert;
import org.junit.Test;

public class DateUtilsTest {

    @Test
    public void mes(){
        Assert.assertEquals("07", DateUtils.mesString(7));
        Assert.assertEquals("11", DateUtils.mesString(11));
        Assert.assertEquals("05", DateUtils.mesString(5));
    }

    @Test
    public void ano(){
        int ano = 2011;
        String novo = DateUtils.anoString(ano);
        Assert.assertEquals("2011", novo);
    }
}
