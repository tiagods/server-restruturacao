package com.tiagods.prolink.utils;

import org.junit.Assert;
import org.junit.Test;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Test
    public void testePeriodos() {
        LocalDate novembro = LocalDate.of(2020, Month.NOVEMBER, 01);
        LocalDate agosto = LocalDate.of(2020, Month.AUGUST, 01);
        Set<LocalDate> list = DateUtils.gerarPeriodosProcessamento(novembro);
        Set<LocalDate> datas = new TreeSet<>(
                Stream.of("2019-09-01",
                "2019-10-01", "2019-11-01", "2019-12-01", "2020-01-01",
                "2020-02-01", "2020-03-01", "2020-04-01", "2020-05-01",
                "2020-06-01", "2020-07-01", "2020-08-01")
                .map(LocalDate::parse)
                .collect(Collectors.toList())
        );
        Assert.assertEquals(datas, list);
        list.stream().filter(filter -> filter.isAfter(agosto)).findFirst()
                .ifPresent(v->Assert.fail(v.toString()));
    }
}
