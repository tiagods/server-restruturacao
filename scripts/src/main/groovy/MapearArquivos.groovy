import groovy.transform.Field

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors

@Field String path = '\\\\plkserver\\Contabil\\2020\\Demonstracoes contabeis ECD 2021 para validar com certificado digital'

def iniciar() {
    Path repositorio = Paths.get(path)

    /*
    String collect = Files.list(repositorio)
            .filter({ it ->
                Files.isRegularFile(it)
            }).collect(Collectors.joining(System.getProperty('line.separator')))
*/
    File newFile = new File('collect.txt')
    //newFile.text = collect

    newFile
}

println 'Iniciando processo...'
iniciar()
println 'Processo finalizado...'