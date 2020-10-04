package com.tiagods.gfip;

import com.tiagods.gfip.model.Arquivo;
import com.tiagods.gfip.repository.ArquivoRepository;
import com.tiagods.gfip.repository.ChaveRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class TesteBase {

    @Autowired
    private ArquivoRepository arquivoRepository;

    @Autowired
    private ChaveRepository chaveRepository;

    @Test
    public void testeBanco() {
        Arquivo arquivo = new Arquivo();
        arquivo.setDiretorio("fake");
        arquivo.setPeriodo(LocalDate.now());
        arquivoRepository.save(arquivo);
    }
}
