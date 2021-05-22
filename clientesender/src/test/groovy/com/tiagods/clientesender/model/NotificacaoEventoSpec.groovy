package com.tiagods.clientesender.model

import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths

class NotificacaoEventoSpec extends Specification {

    def 'montar notificacaoevento'() {
        given:
        EmailDto emailDto = new EmailDto()
        emailDto.setOutrosAnexos("anexo1;anexo2")

        Map<String, Path> map = new HashMap()

        map.put("file1", Paths.get("path/file1"))
        map.put("file2", Paths.get("path/file2"))
        map.put("file3", Paths.get("path/file3"))

        emailDto.setAttachs(map)

        when:
        NotificacaoEvento notificacaoEvento = new NotificacaoEvento(1L,
                new Cliente(),
                'processoA', emailDto)

        then:
        println(notificacaoEvento.anexos)
        notificacaoEvento != null

        and:
        notificacaoEvento.getAnexos()[0] == "path/file2"
        notificacaoEvento.getAnexos()[1] == "path/file3"
        notificacaoEvento.getAnexos()[2] == "path/file1"
        notificacaoEvento.getAnexos()[3] == "anexo1"
        notificacaoEvento.getAnexos()[4] == "anexo2"
    }
}
