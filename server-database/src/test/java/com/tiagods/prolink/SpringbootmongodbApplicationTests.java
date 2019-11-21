package com.tiagods.prolink;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class SpringbootmongodbApplicationTests {

	@Test
	void contextLoads() {
		String valor = "E:\\Obrigacoes\\contabil\\Contabil\\SIMPLES NACIONAL-DEFIS\\SIMPLES NACIONAL 2008\\0264_DeclaracaoDASN-041103942007001.pdf";
		int loc = valor.lastIndexOf("\\");
		String newValue = valor.substring(loc+1);
		Assertions.assertEquals("0264_DeclaracaoDASN-041103942007001.pdf",newValue);
	}

}
