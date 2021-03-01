package com.tiagods.obrigacoes.obrigacao;

import com.tiagods.obrigacoes.exception.ParametroIncorretoException;
import com.tiagods.obrigacoes.exception.ParametroNotFoundException;
import com.tiagods.obrigacoes.model.Obrigacao;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.time.Month;
import java.time.Year;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ObrigacaoFactoryTest {

    private static Obrigacao obrigacao;

    @BeforeClass
    public static void init(){
        obrigacao = new Obrigacao();
        obrigacao.setAno(Year.of(2011));
        obrigacao.setMes(Month.AUGUST);
        obrigacao.setDirForJob("c:\\Temp\\PROLINK DIGITAL");
        obrigacao.setTipo(Obrigacao.Tipo.PROLINKDIGITAL);
        obrigacao.setCliente(2045L);
    }

    @Test
    public void inclusaoPeriodos() {
        ObrigacaoContrato ob = ObrigacaoFactory.get(obrigacao);
        Assert.assertTrue(ob.contains(Periodo.ANO));
        Assert.assertTrue(ob.contains(Periodo.MES));
    }

    @Test
    public void periodoAno() {
        ObrigacaoContrato ob = ObrigacaoFactory.get(obrigacao);
        try {
            Assert.assertEquals("2011", ob.getMesOuAno(Periodo.ANO, "2011"));
        } catch (ParametroNotFoundException e) {
            Assert.fail();
        } catch (ParametroIncorretoException e) {
            Assert.fail();
        }
    }

    @Test
    public void periodoMes() {
        ObrigacaoContrato ob = ObrigacaoFactory.get(obrigacao);
        try {
            Assert.assertEquals("08", ob.getMesOuAno(Periodo.MES, "PROLINK DIGITAL 08-2011"));
        } catch (ParametroNotFoundException e) {
            Assert.fail();
        } catch (ParametroIncorretoException e) {
            Assert.fail();
        }
    }

    @Test
    public void nomePastaMes() {
        ObrigacaoContrato ob = ObrigacaoFactory.get(obrigacao);
        try {
            Assert.assertEquals("08", ob.getMesOuAno(Periodo.MES, "PROLINK DIGITAL 08-2011"));
        } catch (ParametroNotFoundException e) {
            Assert.fail();
        } catch (ParametroIncorretoException e) {
            Assert.fail();
        }
    }

    @Test
    public void validarObrigacaoAnual() {
        Obrigacao obr = obrigacao;
        obr.setTipo(Obrigacao.Tipo.IRPF);
        ObrigacaoContrato ob = ObrigacaoFactory.get(obr);
        try {
            //nao existe irpf para mes, apenas ano
            Assert.assertEquals("11", ob.getMesOuAno(Periodo.MES, "04"));
            Assert.fail();
        } catch (ParametroNotFoundException e) {
            Assert.assertEquals("Periodo invalido para a obrigação=[Periodo=MES][Pasta=04]", e.getMessage());
        } catch (ParametroIncorretoException e) {
            Assert.fail();
        }
    }

    @Test
    public void pastaAnoInvalida() {
        ObrigacaoContrato ob = ObrigacaoFactory.get(obrigacao);
        try {
            Assert.assertEquals("2011", ob.getMesOuAno(Periodo.ANO, "-2011"));
            Assert.fail();
        } catch (ParametroNotFoundException e) {
            Assert.fail();
        } catch (ParametroIncorretoException e) {
            Assert.assertEquals("O nome da pasta esta inconsistente=[Periodo=ANO][Pasta=-2011]", e.getMessage());
        }
    }
}
