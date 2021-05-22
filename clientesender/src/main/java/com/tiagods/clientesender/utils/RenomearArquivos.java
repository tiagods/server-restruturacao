package com.tiagods.clientesender.utils;

import lombok.extern.slf4j.Slf4j;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Slf4j
public class RenomearArquivos {

    public static void main(String[] args) {
        renomear();
    }

    public static void renomear() {
        String regex = "^ok\\s?[0-9]{4}+[^0-9].*balancete.*\\.pdf$";

        try {
            Path path = Paths.get("\\\\plkserver\\Contabil\\2020\\Balancete e DRE para  analise\\BALANCOS PARA ENVIAR AO CLIENTE");
            List<Path> files = Files.walk(path)
                    .filter(f -> Files.isRegularFile(f) &&
                            f.getFileName().toString().toLowerCase().matches(regex))
                    .collect(Collectors.toList());
            log.info("Total de arquivos encontrados=({})", files.size());

            StringBuilder st = new StringBuilder();
            for (Path file : files) {
                st.append(move(file));
            }

            gerarRelatorio(st.toString());
        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String move(Path file) throws IOException {
        String newFileName = file.getFileName().toString().replace("OK","").trim();
        Path newFile = file.getParent().resolve(newFileName);

        log.info("Movendo arquivo de=({}) para=({})", file.toString(), newFile.toString());


        Files.move(file, newFile);

        StringBuilder st = new StringBuilder();
        st.append(file.toString())
                .append(";")
                .append(newFile.toString())
                .append(System.getProperty("line.separator"));

        return st.toString();
    }

    public static void gerarRelatorio(String texto) throws IOException {
        String fileName = "Renomear_Historico-"
                .concat(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")))
                .concat(".txt");
        Path arquivo = Paths.get(System.getProperty("user.dir"), "historico", fileName);

        if(Files.notExists(arquivo.getParent())) {
            Files.createDirectory(arquivo.getParent());
        }

        FileWriter fw = new FileWriter(arquivo.toFile(), true);
        fw.write(texto);
        fw.flush();
        fw.close();
    }
}
