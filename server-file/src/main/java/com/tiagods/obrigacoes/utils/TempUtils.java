package com.tiagods.obrigacoes.utils;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class TempUtils {
    public static void createFileTemp(Path path, boolean create) {
        Stream.iterate(1, n -> n + 1)
                .limit(12)
                .forEach(fileName -> {
                    Path pathFile = path.resolve(fileName + ".txt");
                    try {
                        if (Files.notExists(path)) Files.createDirectories(path);
                        FileUtils.write(pathFile.toFile(), "qualquer coisa", Charset.defaultCharset());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        Path newFolder = path.resolve("1011");
        if(create) createFileTemp(newFolder, false);
    }
}
