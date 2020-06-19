package com.tiagods.prolink;

import org.junit.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpringbootfileApplicationTests {
	static Path base = Paths.get("c:\\clientes");
	private static Path desligados = base.resolve("_desligados_extintas");
	static String regex = "[0-9]{4}+[^0-9]*$";

	//@Test
	public void folderNotEmpty() throws IOException {
		Stream<Path> ativos = Files.list(base);
		Stream<Path> inativos = Files.list(desligados);
		Assert.assertFalse(ativos.collect(Collectors.toList()).isEmpty());
		Assert.assertFalse(inativos.collect(Collectors.toList()).isEmpty());
	}
	//@Test
	public void validNameFolder(){
		String nome = "0192-STUDIO HILUX";
		Assert.assertTrue(nome.matches(regex));
	}

	//buscando pastas de clientes, com id duplicados
	//@Test
	public void searchDuplicateClientFolder() throws IOException {
		Set<Path> actives = Files.list(base)
				.filter(f->Files.isDirectory(f) && f.getFileName().toString()
						.matches(regex))
				.collect(Collectors.toSet());

		Set<Path> desl = Files.list(desligados)
				.filter(f->Files.isDirectory(f) && f.getFileName().toString()
						.matches(regex))
				.collect(Collectors.toSet());

		Set<Path> files = new HashSet<>();
		files.addAll(actives);
		files.addAll(desl);

		Map<String, Long> collect = files
				.stream()
				.collect(
						Collectors.groupingBy(c ->
										c.getFileName().toString().substring(0, 4),
								Collectors.counting()));

		Assert.assertFalse(collect.values().stream().anyMatch(c->c>1));
	}
}
