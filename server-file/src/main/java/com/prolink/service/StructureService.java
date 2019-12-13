package com.prolink.service;

import java.nio.file.Path;

public interface StructureService {
    Path getBase();
    Path getShutdown();
    Path getModel();
}
