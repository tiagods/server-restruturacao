package com.tiagods.gfip.services;

import com.tiagods.gfip.model.Chave;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
public class MapearGfipTest {

    @Test
    public void testar_recuperacao_de_periodo(){
        MapearGfip mapearGfip = new MapearGfip();

        List<Chave> list = new ArrayList<>();
        Stream.of(
                null, null, null,
                LocalDate.of(2020, 01, 01))
                .forEach(localDate -> list.add(Chave.builder().periodo(localDate).build()));

        Optional<LocalDate> result = mapearGfip.coletarData(list);
        Assert.assertEquals(result.get(), LocalDate.of(2020, 01, 01));
    }
}
