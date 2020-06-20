package com.tiagods.prolink.services;

import com.tiagods.prolink.exception.ParametroIncorretoException;
import com.tiagods.prolink.exception.ParametroNotFoundException;
import com.tiagods.prolink.exception.PathInvalidException;
import com.tiagods.prolink.model.Obrigacao;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ObrigacaoTest extends ObrigacaoPreparedServiceTest {

    @Test
    public void testarProlinkDigitalAnoMesCliente() {
        Obrigacao obrigacao = montarObrigacaoFakeAnoMesCliente(Obrigacao.Tipo.PROLINKDIGITAL);
        try {
            moverPastaClientesEValidar(obrigacao);
        } catch (IOException | ParametroNotFoundException | PathInvalidException | ParametroIncorretoException e) {
            Assert.fail();
        }
    }

    @Test
    public void testarProlinkDigitalAnoMes() {
        Obrigacao obrigacao = montarObrigacaoFakeAnoMesCliente(Obrigacao.Tipo.PROLINKDIGITAL);
        obrigacao.setCliente(null);
        try {
            moverPastaClientesEValidar(obrigacao);
        } catch (IOException | ParametroNotFoundException | PathInvalidException | ParametroIncorretoException e) {
            Assert.fail();
        }
    }

    @Test
    public void testarProlinkDigitalAno() {
        Obrigacao obrigacao = montarObrigacaoFakeAnoMesCliente(Obrigacao.Tipo.PROLINKDIGITAL);
        obrigacao.setCliente(null);
        obrigacao.setMes(null);
        try {
            moverPastaClientesEValidar(obrigacao);
        } catch (IOException | ParametroNotFoundException | PathInvalidException | ParametroIncorretoException e) {
            Assert.fail();
        }
    }
}
