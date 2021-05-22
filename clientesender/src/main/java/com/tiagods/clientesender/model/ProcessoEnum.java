package com.tiagods.clientesender.model;

public enum ProcessoEnum {
    BALANCETE(3, 3, 7),
    SPED(1, 0, 0);

    int envios;
    int envio2;
    int envio3;

    ProcessoEnum(int envios, int envio2, int envio3) {
        this.envios = envios;
        this.envio2 = envio2;
        this.envio3 = envio3;
    }

    public int getEnvios() {
        return envios;
    }

    public int getEnvio2() {
        return envio2;
    }

    public int getEnvio3() {
        return envio3;
    }
}
