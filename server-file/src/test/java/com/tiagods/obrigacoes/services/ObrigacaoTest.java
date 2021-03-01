package com.tiagods.obrigacoes.services;

import com.tiagods.obrigacoes.exception.ParametroIncorretoException;
import com.tiagods.obrigacoes.exception.ParametroNotFoundException;
import com.tiagods.obrigacoes.exception.PathInvalidException;
import com.tiagods.obrigacoes.model.Obrigacao;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

@Slf4j
public class ObrigacaoTest extends ObrigacaoPreparedServiceTest {

    @Test
    public void testarProlinkDigitalAnoMesCliente() {
        Obrigacao obrigacao = montarObrigacaoFakeAnoMesCliente(Obrigacao.Tipo.PROLINKDIGITAL);
        try {
            String cid = "cid-test";
            moverPastaClientesEValidar(cid, obrigacao);
        } catch (IOException | ParametroNotFoundException | PathInvalidException | ParametroIncorretoException e) {
            Assert.fail();
        }
    }

    @Test
    public void testarProlinkDigitalAnoMes() {
        Obrigacao obrigacao = montarObrigacaoFakeAnoMesCliente(Obrigacao.Tipo.PROLINKDIGITAL);
        obrigacao.setCliente(null);
        try {
            String cid = "cid-test";
            moverPastaClientesEValidar(cid, obrigacao);
        } catch (IOException | ParametroNotFoundException | PathInvalidException | ParametroIncorretoException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testarProlinkDigitalAno() {
        Obrigacao obrigacao = montarObrigacaoFakeAnoMesCliente(Obrigacao.Tipo.PROLINKDIGITAL);
        obrigacao.setCliente(null);
        obrigacao.setMes(null);
        try {
            String cid = "cid-test";
            moverPastaClientesEValidar(cid, obrigacao);
        } catch (IOException | ParametroNotFoundException | PathInvalidException | ParametroIncorretoException e) {
            Assert.fail();
        }
    }

    @Test
    public void testarDirfAnoMesCliente() {
        Obrigacao obrigacao = montarObrigacaoFakeAnoMesCliente(Obrigacao.Tipo.DIRF);
        try {
            String cid = "cid-test";
            moverPastaClientesEValidar(cid, obrigacao);
        } catch (IOException | ParametroNotFoundException | PathInvalidException | ParametroIncorretoException e) {
            Assert.fail();
        }
    }

    @Test
    public void testarDirfAnoMes() {
        Obrigacao obrigacao = montarObrigacaoFakeAnoMesCliente(Obrigacao.Tipo.DIRF);
        obrigacao.setCliente(null);
        try {
            String cid = "cid-test";
            moverPastaClientesEValidar(cid, obrigacao);
        } catch (IOException | ParametroNotFoundException | PathInvalidException | ParametroIncorretoException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testarDirfAno() {
        Obrigacao obrigacao = montarObrigacaoFakeAnoMesCliente(Obrigacao.Tipo.DIRF);
        obrigacao.setCliente(null);
        obrigacao.setMes(null);
        try {
            String cid = "cid-test";
            moverPastaClientesEValidar(cid, obrigacao);
        } catch (IOException | ParametroNotFoundException | PathInvalidException | ParametroIncorretoException e) {
            Assert.fail();
        }
    }

    @Test
    public void testarIRPFAnoMesCliente() {
        Obrigacao obrigacao = montarObrigacaoFakeAnoMesCliente(Obrigacao.Tipo.IRPF);
        try {
            String cid = "cid-test";
            moverPastaClientesEValidar(cid, obrigacao);
            Assert.fail();
        } catch (IOException | ParametroNotFoundException | PathInvalidException | ParametroIncorretoException e) {
            log.error(e.getMessage());
            Assert.assertEquals(e.getMessage(), "Obrigação IRPF é anual e o mês não deve ser informado");
        }
    }

    @Test
    public void testarIRPFAnoCliente() {
        Obrigacao obrigacao = montarObrigacaoFakeAnoMesCliente(Obrigacao.Tipo.IRPF);
        obrigacao.setMes(null);
        try {
            String cid = "cid-test";
            moverPastaClientesEValidar(cid, obrigacao);
        } catch (IOException | ParametroNotFoundException | PathInvalidException | ParametroIncorretoException e) {
            Assert.fail();
        }
    }

    @Test
    public void testarIRPFAno() {
        Obrigacao obrigacao = montarObrigacaoFakeAnoMesCliente(Obrigacao.Tipo.IRPF);
        obrigacao.setCliente(null);
        obrigacao.setMes(null);
        try {
            String cid = "cid-test";
            moverPastaClientesEValidar(cid, obrigacao);
        } catch (IOException | ParametroNotFoundException | PathInvalidException | ParametroIncorretoException e) {
            Assert.fail();
        }
    }
}
