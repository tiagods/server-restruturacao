package com.tiagods.prolink.job;

import com.tiagods.prolink.model.Cliente;
import com.tiagods.prolink.service.ClienteIOService;
import com.tiagods.prolink.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

@Service
public class MoverArquivo {

    private Set<Cliente> clienteSet;

    private Path novaEstrutura;


    @Autowired
    IOUtils ioUtils;

    ClienteIOService clientIOService;

    private void iniciar(){
        Path path = Paths.get("\\\\PLKSERVER\\Obrigacoes\\contabil\\Contabil\\SPED CONTÁBIL");
        novaEstrutura = Paths.get("Obrigacoes","SPED CONTABIL", "2019");

        try {
            Iterator<Path> files = Files.list(path).iterator();
            clientIOService.verifyStructureInModel(novaEstrutura);

            processar(files, OrdemBusca.ID,null,1);
            //processar(files, OrdemBusca.CNPJ,null,1);
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    private void processar(Iterator<Path> files, OrdemBusca ordemBusca,String regex,int index) throws IOException{
        while (files.hasNext()) {
            Path arquivo  = files.next();
            if(Files.isDirectory(arquivo)){
                processar(Files.list(arquivo).iterator(), ordemBusca,regex,index);
            }
            else {
                if(ordemBusca.equals(OrdemBusca.CNPJ)) {
                    Path pathCli = buscarPorCnpj(arquivo, clienteSet, OrdemV1.INICIO);
                    if (pathCli != null) mover(arquivo, pathCli);
                }
                else{
                    Path pathCli = buscarPorId(arquivo,clienteSet,regex,index);
                    if(pathCli !=null) mover(arquivo,pathCli);
                }
            }
        }
    }
    private void mover(Path arquivo, Path pathCli) throws IOException{
        Path novoArquivo = novaEstrutura.resolve(arquivo.getFileName());
        Path arquivoFinal = pathCli.resolve(novoArquivo);
        if(Files.notExists(arquivoFinal.getParent())) Files.createDirectories(arquivoFinal.getParent());
        Files.move(arquivo, arquivoFinal, StandardCopyOption.REPLACE_EXISTING);
        System.out.println(arquivo + "\t>\t" + arquivoFinal);
        //salvarRelatorio(arquivo.toString(),arquivoFinal.toString());
    }

    private Path buscarPorCnpj(Path arquivo, Set<Cliente> clienteSet, OrdemV1 ordemV1){
        if(arquivo.getFileName().toString().trim().length()<14) return null;
        if(ordemV1.equals(OrdemV1.INICIO)) {
            String cnpj = arquivo.getFileName().toString().substring(0,14);
            Optional<Cliente> cliente = clienteSet.stream().filter(c -> c.isCnpjValido() && c.getCnpjFormatado().equals(cnpj)).findAny();
            return cliente.isPresent()? clientIOService.searchClientPathBaseAndCreateIfNotExists(cliente.get()) : null;
        }
        else return null;
    }
    private Path buscarPorId(Path arquivo, Set<Cliente> clienteSet,String regex,int index){
         if(arquivo.getFileName().toString().trim().length()<4) return null;

         if(regex==null){
             try{
                 Integer.parseInt(arquivo.getFileName().toString().substring(0,5));
                 return null;//retornar se id for maior que 4
             }catch (Exception e) {//nao fazer nada
             }
             String valor =  arquivo.getFileName().toString().substring(0,4);
             Optional<Cliente> cliente = clienteSet.stream().filter(c -> c.getIdFormatado().equals(valor)).findAny();
             return cliente.isPresent() ? clientIOService.searchClientPathBaseAndCreateIfNotExists(cliente.get()) : null;
         }
         else{
            String nome = arquivo.getFileName().toString();
            String novoNome = nome.contains(".")?nome.substring(0,nome.lastIndexOf(".")):nome;
            String[] array = (novoNome).split(regex);
            if (array.length > index && array[index].length() >= 4) {
                try{
                    if(array[index].length()>4) {
                        Integer.parseInt(array[index].substring(0, 5));
                        return null;//retornar se id for maior que 4
                    }
                }catch (Exception e) {
                    //nao fazer nada
                }
                Optional<Cliente> cliente = clienteSet.stream().filter(c -> c.getIdFormatado().equals(array[index])).findAny();
                return cliente.isPresent() ? clientIOService.searchClientPathBaseAndCreateIfNotExists(cliente.get()) : null;
            }
            else return null;
         }
    }
}
