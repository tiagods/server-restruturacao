package com.tiagods.prolink;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class PrintSubpaths {

    public static void main(String[] args) throws IOException {
        String cid = "cid-test";

        Path pastaCliente = Paths.get("c:/temp/2222");

        Path estrutura = Paths.get("GERAL/SAC");

        String nome = "arquivo.txt";

        Path estruturaFinal = pastaCliente.resolve(estrutura);

        log.info("Correlation: [{}]. Nome do arquivo: ({}) para ({})", cid, nome, nome);
        Path newStructureFile = estruturaFinal.resolve(nome);
        log.info("Correlation: [{}]. Nova estrutura de arquivo: ({})", cid, newStructureFile.toString());
        Path finalFile = pastaCliente.resolve(newStructureFile);
        log.info("Correlation: [{}]. Nome final de arquivo: ({})", cid, finalFile.toString());

    }
}
