package com.prolink.service;

import com.prolink.exception.StructureNotFoundException;
import com.prolink.olders.model.Cliente;
import com.prolink.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClientIOService {
    @Autowired
    private StructureService structureService;

    @Autowired
    private IOUtils ioUtils;

    private Map<Cliente, Path> cliMap = new HashMap<>();


    public Path searchClient(Cliente c) {
        return cliMap.get(c);
    }

}
