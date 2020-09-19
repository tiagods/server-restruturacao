package com.tiagods.prolink.io;

import com.tiagods.prolink.config.Regex;
import com.tiagods.prolink.dto.ArquivoErroDTO;
import com.tiagods.prolink.model.Cliente;
import com.tiagods.prolink.model.Pair;
import com.tiagods.prolink.dao.ArquivoDAOService;
import com.tiagods.prolink.service.ClienteService;
import com.tiagods.prolink.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class IOService {

    @Autowired private ArquivoDAOService arquivoDAOService;
    @Autowired private Regex regex;
    @Autowired private ClienteService clienteService;

    //tentar mover, se nao conseguir usar o diretorio de origem
    public Pair<Cliente, Path> mover(String cid, Cliente cliente, Path origin, Path destination){
        try{
            log.info("Correlation: [{}]. Movendo [{}] para [{}]", cid, origin, destination);
            Files.move(origin, destination, StandardCopyOption.REPLACE_EXISTING);
            arquivoDAOService.convertAndSave(cid, origin, destination, cliente);
            return new Pair<>(cliente, destination);
        }catch (IOException e){
            log.error(e.getMessage());
            return new Pair<>(cliente, origin);
        }
    }

    //mover arquivo com estrutura pre estabelecida
    public Path mover(String cid, Cliente cli, boolean renomearSemId, Path arquivo, Path pastaCliente, Path estrutura){
        String nome = arquivo.getFileName().toString();
        //renomeando path se necessario
        String fileName = renomearSemId && !validarSeIniciaComId(cid, arquivo, cli.getIdFormatado(), true) ?
                    cli.getIdFormatado() + "-" + nome : nome;

        log.info("Correlation: [{}]. Nome do arquivo: ({}) para ({})", cid, nome, fileName);
        Path newStructureFile = estrutura.resolve(fileName);
        log.info("Correlation: [{}]. Nova estrutura de arquivo: ({})", cid, newStructureFile.toString());
        Path finalFile = pastaCliente.resolve(newStructureFile);
        log.info("Correlation: [{}]. Nova caminho de arquivo: ({})", cid, finalFile.toString());
        try {
            IOUtils.criarDiretorios(finalFile.getParent());
            log.info("Correlation: [{}]. Movendo ({}) para ({})",
                    cid, arquivo.toString(), finalFile.toString());
            Files.move(arquivo, finalFile, StandardCopyOption.REPLACE_EXISTING);
            arquivoDAOService.convertAndSave(cid, arquivo,finalFile, cli);
            return finalFile;
        }catch (IOException e){
            arquivoDAOService.salvarErro(cid, arquivo,finalFile,e.getMessage(), ArquivoErroDTO.Status.ERROR, cli);
            log.error("Correlation: [{}]. Falha ao mover arquivo: ({}), ex:{}", cid, arquivo.toString(), e.getMessage());
            return null;
        }
    }

    public boolean validarSeIniciaComId(String cid, Path file, String idFormatado, boolean salvarErro){
        String valor = file.getFileName().toString();
        boolean matcher1 = valor.matches(regex.getInitById());
        boolean matcher2 = valor.matches(regex.getInitByIdReplaceNickName().replace("nickName", idFormatado));
        if(matcher1 && !matcher2 && salvarErro) {//pode iniciar com o id de outro cliente
            arquivoDAOService.salvarErro(cid, file,null, ArquivoErroDTO.Status.WARN.getDescricao(), ArquivoErroDTO.Status.ERROR, null);
        }
        return matcher2;
    }
}
