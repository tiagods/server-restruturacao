package com.tiagods.clientesender.model

import spock.lang.Specification

class ClienteSpec extends Specification {

    def 'Montar entidade'(Long idCliente, result) {
        setup:
        Cliente cliente = new Cliente();
        cliente.setIdCliente(idCliente)

        expect:
        result == cliente.getApelido()

        where:
        idCliente || result
        1         || "0001"
        11        || "0011"
        111       || "0111"
        1111      || "1111"
    }
}
