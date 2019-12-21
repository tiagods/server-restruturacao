package com.tiagods.prolink.service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tiagods.prolink.dto.ArquivoDTO;
import com.tiagods.prolink.repository.ArquivoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class ArquivoService {
    @Autowired
    ArquivoRepository repository;

    public ArquivoDTO salvar(ArquivoDTO arquivo){
        return repository.save(arquivo);
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
