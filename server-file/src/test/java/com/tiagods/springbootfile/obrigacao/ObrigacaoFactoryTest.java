package com.tiagods.springbootfile.obrigacao;

import com.tiagods.prolink.exception.ParametroIncorretoException;
import com.tiagods.prolink.exception.ParametroNotFoundException;
import com.tiagods.prolink.model.Obrigacao;
import com.tiagods.prolink.obrigacao.ObrigacaoContrato;
import com.tiagods.prolink.obrigacao.ObrigacaoFactory;
import com.tiagods.prolink.obrigacao.Periodo;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Month;
import java.time.Year;

public class ObrigacaoFactoryTest {

    private static Obrigacao obrigacao;

    @BeforeClass
    public static void init(){
        obrigacao = new Obrigacao();
        obrigacao.setAno(Year.of(2011));
        obrigacao.setMes(Month.AUGUST);
        obrigacao.setDirForJob("c:\\Temp\\PROLINK DIGITAL");
        obrigacao.setTipo(Obrigacao.Tipo.PROLINKDIGITAL);
        obrigacao.setCliente(2045);
    }

    @Test
    public void validarInclusaoPeriodos() {
        ObrigacaoContrato ob = ObrigacaoFactory.get(obrigacao);
        Assert.assertTrue(ob.contains(Periodo.ANO));
        Assert.assertTrue(ob.contains(Periodo.MES));
    }

    @Test
    public void validarPeriodoAno() {
        ObrigacaoContrato ob = ObrigacaoFactory.get(obrigacao);
        try {
            Assert.assertEquals("2011", ob.get(Periodo.ANO, "2011"));
        } catch (ParametroNotFoundException e) {
            Assert.fail();
        } catch (ParametroIncorretoException e) {
            Assert.fail();
        }
    }

    @Test
    public void validarPeriodoMes() {
        ObrigacaoContrato ob = ObrigacaoFactory.get(obrigacao);
        try {
            Assert.assertEquals("08", ob.get(Periodo.MES, "PROLINK DIGITAL 08-2011"));
        } catch (ParametroNotFoundException e) {
            Assert.fail();
        } catch (ParametroIncorretoException e) {
            Assert.fail();
        }
    }

    @Test
    public void obrigacaoAnual() {
        Obrigacao obr = obrigacao;
        obr.setTipo(Obrigacao.Tipo.IRPF);
        ObrigacaoContrato ob = ObrigacaoFactory.get(obr);

        try {
            //nao existe irpf para mes, apenas ano
            Assert.assertEquals("11", ob.get(Periodo.MES, "04"));
            Assert.fail();
        } catch (ParametroNotFoundException e) {
            Assert.assertEquals("Periodo invalido para a obrigação", e.getMessage());
        } catch (ParametroIncorretoException e) {
            Assert.fail();
        }
    }

    @Test
    public void pastaAnoInvalida() {
        ObrigacaoContrato ob = ObrigacaoFactory.get(obrigacao);
        try {
            Assert.assertEquals("2011", ob.get(Periodo.ANO, "-2011"));
            Assert.fail();
        } catch (ParametroNotFoundException e) {
            Assert.fail();
        } catch (ParametroIncorretoException e) {
            Assert.assertEquals("O nome da pasta esta inconsistente", e.getMessage());
        }
    }
}
