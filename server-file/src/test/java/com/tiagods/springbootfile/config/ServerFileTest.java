package com.tiagods.springbootfile.config;

import com.tiagods.prolink.config.ServerFile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServerFile.class)
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
