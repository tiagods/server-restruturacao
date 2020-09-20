package com.tiagods.serverconsumer;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ServerConsumerApplicationTests {

    @Test
    public void contextLoads() {
        Assert.assertEquals("Message", "Message");
    }
}
