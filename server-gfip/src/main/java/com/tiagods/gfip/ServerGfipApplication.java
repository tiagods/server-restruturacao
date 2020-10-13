package com.tiagods.gfip;

import com.tiagods.gfip.services.MapearGfip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class ServerGfipApplication extends SpringBootServletInitializer implements CommandLineRunner {

	@Autowired
	MapearGfip mapearGfip;

	public static void main(String[] args) {
		java.util.logging.Logger.getLogger("org.apache.pdfbox")
				.setLevel(java.util.logging.Level.OFF);
		SpringApplication.run(ServerGfipApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ServerGfipApplication.class);
	}

	@Override
	public void run(String... args) throws Exception {
		//mapearGfip.iniciarMapeamento();
	}
}
