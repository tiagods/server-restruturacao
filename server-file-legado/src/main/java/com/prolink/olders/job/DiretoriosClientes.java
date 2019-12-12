package com.prolink.olders.job;

import com.prolink.olders.config.BasePath;
import com.prolink.olders.config.ClienteData;

import java.nio.file.Path;

public class DiretoriosClientes extends BasePath {
    public DiretoriosClientes(){
        super();
    }
    public static void main(String[] args) {
        new DiretoriosClientes().organizar();
    }
    public void organizar(){
        ClienteData data = ClienteData.getInstance();
        Path base = getBase();
    }
}
