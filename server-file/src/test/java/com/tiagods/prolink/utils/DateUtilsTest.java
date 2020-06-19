package com.tiagods.prolink.utils;

import org.junit.Assert;
import org.junit.Test;

import java.time.Month;
import java.time.Year;

public class DateUtilsTest {

    @Test
    public void mes(){
        Assert.assertEquals("09", DateUtils.mesString(Month.of(9)));
        Assert.assertEquals("07", DateUtils.mesString(7));
        Assert.assertEquals("11", DateUtils.mesString(11));
        Assert.assertEquals("05", DateUtils.mesString(5));
    }

    @Test
    public void ano(){
        int ano = 2011;
        String novo = DateUtils.anoString(ano);
        Assert.assertEquals("2011", DateUtils.anoString(Year.of(ano)));
        Assert.assertEquals("2011", novo);
    }
}
