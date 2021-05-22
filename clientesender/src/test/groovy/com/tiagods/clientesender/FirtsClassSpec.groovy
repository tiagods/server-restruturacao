package com.tiagods.clientesender

import lombok.extern.slf4j.Slf4j
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Collectors

@Slf4j
class FirtsClassSpec extends Specification {

    def 'test'(){

        when:
        def result = 1

        then:
        result == 1
    }

    def 'regex validate'() {
    }
}
