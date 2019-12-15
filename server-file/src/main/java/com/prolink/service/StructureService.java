package com.prolink.service;

import java.nio.file.Path;
import java.util.Set;

public interface StructureService {
    //listar todos os clientes ativos e inativos, e suas pastas
    Set<Path> listAllInBaseAndShutdown();

    Path getBase();
    Path getShutdown();
    Path getModel();
}
