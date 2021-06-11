package com.tiagods.clientesender;

import com.tiagods.clientesender.model.EmailDto;
import com.tiagods.clientesender.model.ProcessoEnum;
import com.tiagods.clientesender.service.ClienteService;
import com.tiagods.clientesender.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@SpringBootApplication
public class ClientesenderApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ClientesenderApplication.class, args);
	}

	@Autowired
    EmailService emailService;

	@Autowired
	ClienteService clienteService;

	@Override
	public void run(String... args) throws Exception {
		var emailDto = new EmailDto(
				"prolink@prolinkcontabil.com.br",
				"tiagoice@hotmail.com",
				"Estou te enviando email de um servico",
				"Essa é uma mensagem de exemplo"
		);
		var mapa = new HashMap<>();
		mapa.put("name", "Tiago");
		mapa.put("topics", Arrays.asList(new String[]{"Topico A"}));

		Map<String, Path> attachs = new HashMap<>();

		var path = Paths.get("HELP.md");

		attachs.put(path.getFileName().toString(), path);
		attachs.put("File2.md", path);

		emailDto.setAttachs(attachs);

		//emailService.sendHtmlEmail(UUID.randomUUID().toString(), emailDto);

		var clientes = clienteService.listarTodosClientes();

		var cli = clientes.stream().filter(f -> f.getIdCliente() ==17l).findFirst().get();
		log.info("Analizando consulta: clientes=({}), cliente({})", clientes.size(), cli);

		clienteService.inicarPorProcesso(UUID.randomUUID().toString(), ProcessoEnum.BALANCETE);
	}
}
