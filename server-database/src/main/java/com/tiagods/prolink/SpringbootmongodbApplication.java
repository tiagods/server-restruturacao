package com.tiagods.prolink;

import com.tiagods.prolink.model.Arquivo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class SpringbootmongodbApplication implements CommandLineRunner {

	@Autowired
	private ArquivoRepository arquivos;

	public static void main(String[] args) {
		SpringApplication.run(SpringbootmongodbApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		SimpleDateFormat sdf  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		Path path = Paths.get("composicao.csv");
		Scanner scanner = new Scanner(path.toFile()).useDelimiter("\n");

		List<Arquivo> arquivosAll = new ArrayList<>();

		int linha = 0;
		while(scanner.hasNext()){
			String string = scanner.next();
			String[] obj = string.split(";");
			if(obj.length>1) {
				Arquivo a = new Arquivo();
				a.setData(sdf.parse(obj[0]));
				a.setNome(obj[1]);
				a.setNovoNome(obj[2]);
				a.setOrigem(obj[3]);
				a.setDestino(obj[4]);
				arquivosAll.add(a);
			}
		}
		salvar(arquivosAll);
	}

	@Transactional
	public void salvar(List<Arquivo> arquivoAll){
		arquivos.saveAll(arquivoAll);
	}
	/*
	@Override
	public void run(String... args) throws Exception {
		SimpleDateFormat sdf  = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		List<Arquivo> arquivosAll = arquivos.findAll();
		Path path = Paths.get("composicao.csv");
		FileWriter writer = new FileWriter(path.toFile());

		StringBuilder builder = new StringBuilder();
		int tamanho = arquivosAll.size();
		for(int i = 0 ; i<arquivosAll.size(); i++) {
			Arquivo a = arquivosAll.get(i);
			builder.append(sdf.format(a.getData()))
					.append(";")
					.append(a.getNome())
					.append(";")
					.append(a.getNovoNome())
					.append(";")
					.append(a.getOrigem())
					.append(";")
					.append(a.getDestino())
					.append(i+1 == tamanho?"":System.getProperty("line.separator"));
			if(i%100==0){
				writer.write(builder.toString());
				builder = new StringBuilder();
			}
		}
		writer.write(builder.toString());
		writer.flush();
		writer.close();
	}
	*/
}
