package com.tiagods.clientesender

import com.tiagods.clientesender.model.ProcessoEnum
import com.tiagods.clientesender.model.Cliente
import com.tiagods.clientesender.model.NotificacaoControle
import com.tiagods.clientesender.service.ClienteService
import spock.lang.Specification
import java.time.LocalDate
import java.time.ZoneId

class ClienteServiceSpec extends Specification {

    ClienteService clienteService
    String cid = "test"

    def setup() {
        clienteService = Spy(ClienteService)
    }

    def 'metodo buscarClientePorId'() {
        when:
        Optional<Cliente> result = clienteService.buscarClientePorId(22l)

        then:
        assert result.isPresent()
        assert result.get().getIdCliente() == 22l
    }

    def 'metodo recuperarDataEnvio'( ceEnvio, LocalDate ceDate,  pe, LocalDate data) {
        NotificacaoControle nc = new NotificacaoControle()
        nc.setClienteId(1L)

        if(ceDate != null) {
            GregorianCalendar gc = GregorianCalendar.from(ceDate.atStartOfDay(ZoneId.systemDefault()));
            nc.setDate(gc)
        }
        nc.setNumEnvios(ceEnvio)

        expect:
        data == clienteService.recuperarDataEnvio(cid, pe, nc)

        where:
        ceEnvio | ceDate           | pe                         || data
        2 | null | ProcessoEnum.BALANCETE || LocalDate.now() //passando uma data de controle nula
        1 | LocalDate.now() | ProcessoEnum.BALANCETE || LocalDate.now() //passando o envio = 1
        2 | LocalDate.now() | ProcessoEnum.SPED || LocalDate.now() //passando uma obrigacao de apenas 1 envio
        2 | LocalDate.of(2021, 05, 1) | ProcessoEnum.BALANCETE || LocalDate.of(2021, 05, 4) //passando uma obrigacao balancete com 2 envio
        3 | LocalDate.of(2021, 05, 1) | ProcessoEnum.BALANCETE || LocalDate.of(2021, 05, 8) //passando uma obrigacao balancete com 3 envio
    }

    def "metodo de validarData"(LocalDate dataLiberada, boolean result) {
        ProcessoEnum pe =  ProcessoEnum.BALANCETE
        NotificacaoControle nc = new NotificacaoControle()
        nc.setClienteId(1L)

        expect:
        result == clienteService.validarDataParaEnvio(cid, LocalDate.of(2021, 05, 10), dataLiberada, pe, nc)

        where:
        dataLiberada               || result
        LocalDate.of(2021, 05, 9)  || true
        LocalDate.of(2021, 05, 10) || true
        LocalDate.of(2021, 05, 11) || false
        LocalDate.of(2021, 05, 12) || false
    }

}
