package com.tiagods.obrigacoes.job;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Correcao {

    public static void main(String[] args){
        new Correcao().iniciar();
    }
    private void iniciar(){
        String ex = "E:\\Clientes\\1202-FC KIDS COMERCIO\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\1202- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\1790-CASA DO VIVER BEM\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\1790- 2016_2015 - STDA - Declaração Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\1790-CASA DO VIVER BEM\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\1790- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\1845-PES SEM DOR LTDA\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\1845- 2016_2015 - STDA - Declaração_Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\1845-PES SEM DOR LTDA\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\1845- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\1863-LUCIA REIKO KONISHI\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\1863 - 2016_2015 -STDA - Recibo Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\1903-BAGSAC PINHEIRO\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\1903- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\1917-AP&JP1 COMERCIO\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\1917 - 2016_2015 - STDA - Recibo Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\1940-FLYING FEET\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\1940- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\1954-ASAPH SOLUCOES\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\1954- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\2020-SITE DOS PES LTDA\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2020- 2016_2015 - STDA - Declaração Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\2020-SITE DOS PES LTDA\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2020- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\2072-PES SEM DOR LTDA\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2072- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\2078-THOMAS AMOS CASE\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2078- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\2115-RADIOATIVE COMERCIO\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2115- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\2139-FRUTARIA TROPICANA\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2139- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\2140-SUGURI PAO E CAFE\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2140- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\2163-MOTO GUILHERME\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2163- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\2204-VPA COMERCIO\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2204- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\2215-KM KIDS COMERCIO\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2215- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\2218-MEL E HORTELA\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2218- 2016_2015 - STDA - Declaração Substituto Tribut.pdf;" +
                "E:\\Clientes\\2218-MEL E HORTELA\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2218- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\2226-FLYING FEET\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2226- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\2238-BEMESTAR PRODUTOS\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2238- 2016_2015 - STDA - Declaração Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\2238-BEMESTAR PRODUTOS\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2238- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\2244-VFUNG COMERCIO\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2244- 2016_2015 - STDA - Declaração Substituto Tribut.pdf;" +
                "E:\\Clientes\\2244-VFUNG COMERCIO\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2244- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\2253-NPC1 DISTRIBUIDORA\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2253- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\2267-TILLIA COMERCIO\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2267 - 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\2267-TILLIA COMERCIO\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2267- 2016_2015 - STDA - Declaração_Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\2285-PAU BRASIL COMERCIO\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2285 -2016_2016 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\2285-PAU BRASIL COMERCIO\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2285- 2016_2015 - STDA - Declaração_Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\2291-SONHO DE CHOCOLATE\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2291- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\2296-K&K ASSISTENCIA\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2296 - 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\2296-K&K ASSISTENCIA\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2296- 2016_2015 - STDA - Declaração_Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\2304-G M M FERREIRA\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2304 - 2016_ 2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\2304-G M M FERREIRA\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2304- 2016_2015 - STDA - Declaração_Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\2305-TREMBOM COMERCIO\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2305 - 2016_2015 - STDA - Declaração_Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\2305-TREMBOM COMERCIO\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2305 - 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\2316-MEXA SEGURANCA\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2316- 2016_2015 - STDA - Declaração Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\2316-MEXA SEGURANCA\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2316- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\2317-DGG RESTAURANTE\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2317- 2016_2015 - STDA - Declaração Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\2317-DGG RESTAURANTE\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2317- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\2326-VERTROS TECNOLOGIA\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2326- 2016_2015 - STDA - Declaração Substituto Tribut.pdf;" +
                "E:\\Clientes\\2326-VERTROS TECNOLOGIA\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2326- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\2348-FABRICA AUGUSTA\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2348- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\_desligados\\2349-FABRICA DRINKS\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2349- 2016_2015 - STDA - Recibo - Substituto Tribut.pdf;" +
                "E:\\Clientes\\2382-MC KIDS COMERCIO\\Obrigacoes\\STDA SIMPLES NACIONAL\\2011\\2382- 2016_2015 - STDA - Recibo - Substituto Tribut.PNG;";

        String[] expre = ex.split(";");
        for(String value : expre){
            if(Files.exists(Paths.get(value))) {
                Path path = Paths.get(value).getParent();
                Path pai = path.getParent();
                String newName = path.getFileName().toString().replace("2011","2016");
                try {
                    System.out.println("old"+path+" \t -> \t"+pai.resolve(newName));
                    //Files.move(path,pai.resolve(newName),StandardCopyOption.REPLACE_EXISTING);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            else System.out.println("Not exists:"+Paths.get(value));
        }
    }
}
