package com.tiagods.toolgfip;

import com.tiagods.toolgfip.services.GfipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@Slf4j
public class ToolGfipApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ToolGfipApplication.class, args);
	}

	@Autowired
	GfipService gfipService;
//
//	public void run1(String... args) throws Exception {
//		List<ChaveGfip> gfips = chaveGfipRepository.findAll()
//				.stream()
//				.filter(c -> c.getPaginas().size() > 1)
//				.collect(toList());
//		log.info("Total de registros encontrados: {}", gfips.size());
//		log.info("Total de registros com mais de 2: {}", gfips.stream().filter(c->c.getPaginas().size()>2).count());
//
//		gfips.forEach(c-> {
//			log.info("Tratando registro: ({}) tamanho da lista: {}", c.getPath(), c.getPaginas().size());
//			Set<Integer> set = new TreeSet<>();
//			set.addAll(c.getPaginas());
//			c.setPaginas(new LinkedList<>(set));
//			chaveGfipRepository.save(c);
//		});
//	}

	@Override
	public void run(String... args) throws Exception {
		List<Integer> apelidos = Arrays.asList(
				707,
				585
		);

		for(Integer i : apelidos) {
			String apelido = String.valueOf(i);
			gfipService.iniciarProcessamento(apelido, null);
		}
	}
}
