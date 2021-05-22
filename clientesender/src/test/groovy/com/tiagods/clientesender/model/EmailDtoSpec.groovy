package com.tiagods.clientesender.model

import com.tiagods.clientesender.model.EmailDto
import spock.lang.Specification

class EmailDtoSpec extends Specification {

    def 'Quebrar contas de emails com virgula ou ponto e virgula'(){
        when:
        EmailDto dto = new EmailDto("de", "email1, email2; email3, email4, email1")

        then:
        dto.getPara().length == 4
        dto.getPara()[0] == 'email1'
        dto.getPara()[1] == 'email2'
        dto.getPara()[2] == 'email3'
        dto.getPara()[3] == 'email4'
    }
}
