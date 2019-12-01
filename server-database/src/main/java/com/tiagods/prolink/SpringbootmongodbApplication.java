package com.tiagods.prolink;

import com.tiagods.prolink.model.Cliente;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringbootmongodbApplication {
	public static void main(String[] args) {
		Cliente c = new Cliente();;
		SpringApplication.run(SpringbootmongodbApplication.class, args);
	}
}
