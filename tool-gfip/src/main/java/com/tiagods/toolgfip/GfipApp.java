package com.tiagods.toolgfip;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class GfipApp {
    public static void main(String[] args) {
        try{
            Path file = Paths.get("c:/TEMP/Demonstrativo.pdf");

            PDDocument document = PDDocument.load(file.toFile());
            PDDocument documentNew = new PDDocument();

            List<Integer> paginas = Arrays.asList(1);
            for(Integer page : paginas) {
                PDPage pdPage = document.getPage(page-1);
                documentNew.addPage(pdPage);
            }

            String apelido = "1111";
            //colocando id no nome caso nao exista
            String regex = "(^[\\d]{4})+[^\\d].*";
            String newName = file.getFileName().toString().matches(regex)?
                    file.getFileName().toString():
                    apelido.concat("-").concat(file.getFileName().toString());

            Path diretorio = file.getParent().resolve("dir");

            if(!Files.exists(diretorio)){
                Files.createDirectories(diretorio);
            }
            Path newFile = diretorio.resolve(newName);
            if(!Files.exists(file)){
                log.info("Arquivo nao existe: {}", file.toString());
            }
            else
                documentNew.save(newFile.toFile());
            documentNew.close();
            document.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
