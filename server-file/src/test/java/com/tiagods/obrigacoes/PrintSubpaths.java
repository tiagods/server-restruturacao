package com.tiagods.obrigacoes;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class PrintSubpaths {

    public static void main(String[] args) throws IOException {
        String cid = "cid-test";

        Path pastaCliente = Paths.get("c:/temp/2222");

        Path estrutura = Paths.get("GERAL/SAC");

        String nome = "1111arquivo.txt";

        //Path estruturaFinal = pastaCliente.resolve(estrutura);

        Path estruturaFinal = estrutura;

        log.info("Correlation: [{}]. Nome do arquivo: ({}) para ({})", cid, nome, nome);
        Path newStructureFile = estruturaFinal.resolve(nome);
        log.info("Correlation: [{}]. Nova estrutura de arquivo: ({})", cid, newStructureFile.toString());
        Path finalFile = pastaCliente.resolve(newStructureFile);
        log.info("Correlation: [{}]. Nome final de arquivo: ({})", cid, finalFile.toString());

        Matcher matcher = Pattern.compile("^([0-9]{4})+([^0-9].*)?$").matcher(nome);
        Matcher matcher2 = Pattern.compile("^[0-9]{4}").matcher(nome);

        boolean find = matcher.find();
        boolean find2 = matcher2.find();

        log.info("Is matchers: {}", find);

        if(find && find2){
            log.info("Name: {}", matcher2.group());
        }
    }
}
