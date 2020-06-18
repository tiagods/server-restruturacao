package com.tiagods.prolink.service;

import com.tiagods.prolink.config.Regex;
import com.tiagods.prolink.dto.ArquivoErroDTO;
import com.tiagods.prolink.model.Cliente;
import com.tiagods.prolink.model.Pair;
import com.tiagods.prolink.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
public class IOService {

    @Autowired private ArquivoDAOService arquivoDAOService;
    @Autowired private Regex regex;


    //tentar mover, se nao conseguir usar o diretorio de origem
    public Pair<Cliente, Path> mover(Cliente client, Path origin, Path destination){
        try{
            log.info("Movendo ["+origin+"] para ["+destination+"]");
            Files.move(origin, destination, StandardCopyOption.REPLACE_EXISTING);
            arquivoDAOService.convertAndSave(origin,destination);
            return new Pair<>(client,destination);
        }catch (IOException e){
            log.error(e.getMessage());
            return new Pair<>(client,origin);
        }
    }

    //mover arquivo com estrutura pre estabelecida
    public Path mover(Cliente cli, boolean renomearSemId, Path file, Path baseCli, Path structure){
        String nome = file.getFileName().toString();
        //renomeando path se necessario
        String fileName = renomearSemId && !validarSeIniciaComId(file, cli.getIdFormatado()) ? cli.getIdFormatado()+"-"+nome : nome;
        Path newStructureFile = structure.resolve(fileName);
        Path finalFile = baseCli.resolve(newStructureFile);
        try {
            IOUtils.criarDiretorios(finalFile.getParent());
            log.info("Movendo ["+file.toString()+"] para ["+finalFile.toString()+"]");
            Files.move(file, finalFile, StandardCopyOption.REPLACE_EXISTING);
            arquivoDAOService.convertAndSave(file,finalFile);
            return finalFile;
        }catch (IOException e){
            arquivoDAOService.salvarErro(file,finalFile,e.getMessage(), ArquivoErroDTO.Status.ERROR);
            log.error(e.getMessage());
            return null;
        }
    }

    public boolean validarSeIniciaComId(Path file, String idFormatado){
        String valor = file.getFileName().toString();
        boolean matcher1 = valor.matches(regex.getInitById());
        boolean matcher2 = valor.matches(regex.getInitByIdReplaceNickName().replace("nickName", idFormatado));
        if(matcher1 && !matcher2) {//pode iniciar com o id de outro cliente
            arquivoDAOService.salvarErro(file,null, ArquivoErroDTO.Status.WARN.getDescricao(), ArquivoErroDTO.Status.ERROR);
        }
        return matcher2;
    }
}
