package com.tiagods.gfip;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.Tesseract1;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootApplication
public class ServerGfipApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(ServerGfipApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ServerGfipApplication.class);
	}

	//@Override
	public void run(String... args) throws Exception {
		ITesseract instance = new Tesseract();
		instance.setLanguage("por");//por,eng
		//instance.setDatapath("c:\\Parametros\\tessdata");
		String file = "\\\\plkserver\\Clientes\\0010-COMPUTER SOLUTION\\Contratos\\_DIGITALIZADOS\\CNPJ.pdf";
		if(!Files.exists(Paths.get(file))) System.out.println("Nao existe");
		String texto = instance.doOCR(new File(file));
		System.out.println(texto);
	}
}
