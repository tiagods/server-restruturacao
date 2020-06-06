package com.tiagods.prolink.service;

import com.tiagods.prolink.repository.ArquivoMonitorRepository;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MonitorService {

    @Autowired
    ArquivoMonitorRepository monitorRepository;

    public byte[] capturarRelatorio(String pasta) {

        InputStream stream = null;
        File file = new File("monitor.txt");
        try {
            StringBuilder builder = new StringBuilder();
            Stream.of(Files.list(Paths.get(pasta)))
                    .filter(f->f.toString().length()>=259)
                    .forEach(f->builder.append(f.toString())
                            .append(";")
                            .append(f.toString().length())
                            .append(System.getProperty("line.separator")));

            FileWriter wr = new FileWriter(file);
            wr.write(builder.toString());
            wr.flush();
            wr.close();

            stream = new FileInputStream(file);
            return IOUtils.toByteArray(stream);
        }catch (IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
