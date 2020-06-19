package com.tiagods.prolink.model;

import org.junit.Assert;
import org.junit.Test;

public class ClienteTest {

    Cliente cliente;

    @Test
    public void clienteCnpjValido() {
        cliente = new Cliente(1l,"Cliente Teste","PLATINA", "01.100.300/0001-10");
        Assert.assertEquals(true, cliente.isCnpjValido());

        cliente = new Cliente(1555l,"Cliente Teste","PLATINA", "01100300000101");
        Assert.assertEquals("1555",cliente.getIdFormatado());
        Assert.assertEquals("01100300000101",cliente.getCnpjFormatado());
        Assert.assertEquals(true, cliente.isCnpjValido());
    }

    @Test
    public void clienteCnpjNaoValido() {
        cliente = new Cliente(1l,"Cliente Teste","PLATINA", "01.100.300/0001-0");
        Assert.assertEquals("0001",cliente.getIdFormatado());
        Assert.assertEquals("",cliente.getCnpjFormatado());
        Assert.assertEquals(false, cliente.isCnpjValido());

    }
}
