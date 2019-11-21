package com.tiagods.serverfile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@SpringBootApplication
public class ServerfileApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ServerfileApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Set<Cliente> clienteSet = new HashSet<>();
		clienteSet.add(new Cliente(1,"Cliente","0001"));
		Map<Cliente, Path> map = clienteSet
				.stream()
				.collect(Collectors.toMap(Function.identity(),null));

		//map.put(new Cliente(1,"Cliente","0001"),"Path do cliente 1");

		System.out.println(map.keySet());


	}

	public class Cliente{
		String apelido;
		String nome;
		Integer id;

		public Cliente(){}

		public Cliente(Integer id){
			this.id=id;
		}
		public Cliente(Integer id,String nome,String apelido){
			this.id=id;
			this.nome=nome;
			this.apelido=apelido;
		}

		public Integer getId() {
			return id;
		}

		public String getApelido() {
			return apelido;
		}

		public String getNome() {
			return nome;
		}

		public void setApelido(String apelido) {
			this.apelido = apelido;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

		@Override
		public String toString() {
			return this.nome+this.apelido;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Cliente cliente = (Cliente) o;
			return Objects.equals(id, cliente.id);
		}

		@Override
		public int hashCode() {

			return Objects.hash(id);
		}
	}
}
