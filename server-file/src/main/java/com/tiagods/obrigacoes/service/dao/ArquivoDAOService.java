package com.tiagods.obrigacoes.service.dao;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tiagods.obrigacoes.dto.ArquivoDTO;
import com.tiagods.obrigacoes.dto.ArquivoErroDTO;
import com.tiagods.obrigacoes.model.Cliente;
import com.tiagods.obrigacoes.repository.ArquivoErroRepository;
import com.tiagods.obrigacoes.service.SqsProducer;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;

@Service
public class ArquivoDAOService {

    @Autowired private ArquivoErroRepository erroRepository;
    @Autowired private SqsProducer sqsProducer;

    /*
    public ArquivoDTO save(ArquivoDTO arquivo){
        return arquivoRepository.save(arquivo);
    }
*/
    public void convertAndSave(String cid, Path file, Path finalFile, Cliente cliente){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String usuario = "robo";
        if (authentication!=null && !(authentication instanceof AnonymousAuthenticationToken)) {
            usuario = authentication.getName();
        }
        ArquivoDTO arquivoDTO = new ArquivoDTO();
        arquivoDTO.setData(new Date());
        arquivoDTO.setDestino(finalFile.toString());
        arquivoDTO.setNovoNome(finalFile.getFileName().toString());
        arquivoDTO.setOrigem(file.toString());
        arquivoDTO.setNome(file.getFileName().toString());
        arquivoDTO.setCliente(cliente.getIdFormatado());
        arquivoDTO.setCorrelation(cid);
        arquivoDTO.setUsuario(usuario);
        String token = RandomStringUtils.random(50, true, true);
        arquivoDTO.setToken(token);
        sqsProducer.sendArquivo(cid, arquivoDTO);
//        save(arquivoDTO);
    }

    public void salvarErro(String cid, Path file, Path finalFile, String error, ArquivoErroDTO.Status status, Cliente cliente) {
        ArquivoErroDTO errorDto = new ArquivoErroDTO();
        errorDto.setData(new Date());
        errorDto.setCause(error);
        errorDto.setOrigem(file.toString());
        errorDto.setDestino(finalFile!=null?finalFile.toString():null);
        errorDto.setStatus(status);
        errorDto.setCliente(cliente!=null?cliente.getIdFormatado():null);
        errorDto.setCorrelation(cid);
        erroRepository.save(errorDto);
    }

    private void saveToJsonFile(ArquivoDTO arquivo){
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            String postJson = mapper.writeValueAsString(arquivo);
            FileOutputStream fileOutputStream = new FileOutputStream(arquivo.getId() + ".json");
            mapper.writeValue(fileOutputStream, arquivo);
            fileOutputStream.close();
        } catch (JsonGenerationException e) {
        } catch (JsonMappingException e) {
        } catch (JsonProcessingException e) {
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }
}
