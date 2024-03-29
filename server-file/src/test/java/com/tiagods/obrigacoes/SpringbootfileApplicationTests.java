package com.tiagods.obrigacoes;

import com.tiagods.obrigacoes.model.Obrigacao;
import org.junit.Assert;
import org.junit.Test;

import java.time.Month;
import java.time.Year;

public class SpringbootfileApplicationTests {

	@Test
	public void test(){
		Obrigacao ob = criarObrigacao(Obrigacao.Tipo.PROLINKDIGITAL, Year.of(2018), Month.AUGUST, 105L, "");
		Obrigacao b2 = criarObrigacao(Obrigacao.Tipo.PROLINKDIGITAL, Year.of(2018), Month.AUGUST, 105L, "");

		boolean result1 = ob.getAno().getValue() == b2.getAno().getValue() && ob.getMes() == b2.getMes() && ob.getCliente() ==b2.getCliente();
		boolean result2 = ob.getAno().getValue() == b2.getAno().getValue() && ob.getMes() == b2.getMes();
		boolean result3 = ob.getAno().getValue() == b2.getAno().getValue();

		Assert.assertTrue(result3);
		Assert.assertTrue(result2);
		Assert.assertTrue(result1);
	}
	private Obrigacao criarObrigacao(Obrigacao.Tipo tipo, Year ano, Month mes, Long cliente, String pasta) {
		return new Obrigacao(
				tipo,
				ano,
				mes,
				cliente,
				pasta
		);
	}
}
