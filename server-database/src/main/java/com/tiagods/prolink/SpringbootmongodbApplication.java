package com.tiagods.prolink;

import com.tiagods.prolink.dto.ClienteDTO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringbootmongodbApplication {
	public static void main(String[] args) {
		ClienteDTO c = new ClienteDTO();;
		SpringApplication.run(SpringbootmongodbApplication.class, args);
	}
}
