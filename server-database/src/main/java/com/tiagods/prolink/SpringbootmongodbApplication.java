package com.tiagods.prolink;

import com.tiagods.prolink.model.Cliente;
import com.tiagods.prolink.repository.ClienteRepository;
import com.tiagods.prolink.service.ArquivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class SpringbootmongodbApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootmongodbApplication.class, args);
	}

	@Autowired
	ArquivoService service;
	@Autowired
	ClienteRepository repository;

	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	public void run(String... args) throws Exception {
		/*
		Arquivo arquivo = new Arquivo();
		arquivo.setData(new Date());
		arquivo.setOrigem("c:/clientes_old/2222-teste.txt");
		//arquivoDTO.setDestino("c:/clientes/2222-teste.txt");
		arquivo.setNome("2222-teste.txt");
		arquivo.setNovoNome("2222-teste.txt");

		arquivo = service.salvar(arquivo);

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		String postJson = mapper.writeValueAsString(arquivo);
		// Save JSON string to file
		FileOutputStream fileOutputStream = new FileOutputStream(arquivo.getId()+".json");
		mapper.writeValue(fileOutputStream, arquivo);
		fileOutputStream.close();

		FileInputStream stream = new FileInputStream(arquivo.getId()+".json");
		ObjectMapper object = new ObjectMapper();

		Arquivo dto = object.readValue(stream, Arquivo.class);
		dto.setId(dto.getId()+99);
		System.out.println(dto.toString());

		service.salvar(dto);

		 */
		Optional<Cliente> cli = repository.findByApelido(2222L);

		Query query = new Query();
		query.with(Sort.by(Sort.Order.desc("apelido")));
		query.limit(1);
		Cliente cliLast = mongoTemplate.findOne(query,Cliente.class);

		if(cli.isPresent())
			System.out.println(cli.get().getApelido()+"-"+cli.get().getStatus()+"-"+cli.get().getNome());
		System.out.println(cliLast.getApelido()+"-"+cli.get().getStatus()+"-"+cliLast.getNome());
	}
}
