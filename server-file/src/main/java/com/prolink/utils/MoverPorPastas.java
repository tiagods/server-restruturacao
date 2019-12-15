package com.prolink.utils;

import com.prolink.model.Cliente;
import com.prolink.model.Ordem;
import com.prolink.model.OrdemBusca;
import com.prolink.service.ClientIOService;
import com.prolink.service.StructureService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MoverPorPastas {

    @Autowired
    private StructureService structureService;

    @Autowired
    private IOUtils ioUtils;

    @Autowired
    private UtilsValidator validator;

    @Autowired
    private ClientIOService clientIOService;

    private Path novaEstrutura;

    private void iniciar(){
        Path path = Paths.get("\\\\PLKSERVER\\Todos Departamentos\\SAC");

        try {
            novaEstrutura = Paths.get("SAC");
            ioUtils.verifyStructureInModel(novaEstrutura);
            //vai mover apenas os arquivos de dentro das pastas, as pastas irao continuar

            Map<Path,String> mapClientes = ioUtils.listByDirectoryDefaultToMap(path, validator.getInitById());

            processar(directories.iterator());

            ioUtils.deleteFolderIfEmptyRecursive(path);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private void processar(Cliente cli, Iterator<Path> files, Path parent) throws IOException{
        while (files.hasNext()) {
            Path arquivo  = files.next();
            if(Files.isDirectory(arquivo)){
                processar(cli, Files.list(arquivo).iterator(), arquivo);
            }
            else {
                moverRecursive(arquivo,);
                if(ordemBusca.equals(OrdemBusca.CNPJ)) {
                    Path pathCli = buscarPorCnpj(arquivo, clienteSet, Ordem.INICIO);
                    if (pathCli != null) mover(arquivo, pathCli);
                }
                else{
                    Path pathCli = buscarPorId(arquivo,clienteSet,regex,index);
                    if(pathCli !=null) mover(arquivo,pathCli);
                }
            }
        }
    }
    private void moverRecursive(Path arquivo, Path pathCli, Path estrutura) throws IOException{
        Path novoArquivo = estrutura.resolve(arquivo.getFileName());
        Path arquivoFinal = pathCli.resolve(novoArquivo);
        if(Files.notExists(arquivoFinal.getParent())) Files.createDirectories(arquivoFinal.getParent());
        Files.move(arquivo, arquivoFinal, StandardCopyOption.REPLACE_EXISTING);
        System.out.println(arquivo + "\t>\t" + arquivoFinal);
        salvarRelatorio(arquivo.toString(),arquivoFinal.toString());
    }

    void salvarRelatorio(String de, String to) throws IOException {
        Path path = Paths.get("result.csv");
        if (Files.notExists(path)) Files.createFile(path);
        FileWriter fw = new FileWriter(path.toFile(), true);
        fw.write(de + "\t" + to);
        fw.write(System.getProperty("line.separator"));
        fw.flush();
        fw.close();
    }

}
