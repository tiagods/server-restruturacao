package com.tiagods.obrigacoes.config;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServerFile.class)
@ActiveProfiles("dev")
public class ServerFileTest {
    @Autowired
    private ServerFile serverFile;

    @Test
    public void testerLoad(){
        Assert.assertNotNull(serverFile.getBase()) ;
        Path path = Paths.get(serverFile.getBase());
        Assert.assertTrue(Files.exists(path));
    }
}
