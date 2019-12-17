package com.prolink.utils;

import com.prolink.model.Cliente;
import com.prolink.model.Ordem;
import com.prolink.model.OrdemBusca;
import com.prolink.service.ClientIOService;
import com.prolink.service.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

@Service
public class MoverArquivo {

    private Set<Cliente> clienteSet;

    private Path novaEstrutura;

    @Autowired
    StructureService structureService;

    @Autowired
    IOUtils ioUtils;

    ClientIOService clientIOService;

    public static void main(String[] args) {
        new MoverArquivo().iniciar();
    }

    private void iniciar(){
        Path path = Paths.get("\\\\PLKSERVER\\Obrigacoes\\contabil\\Contabil\\SPED CONTÁBIL");
        try {
            Iterator<Path> files = Files.list(path).iterator();
            novaEstrutura = Paths.get("Obrigacoes","SPED CONTABIL", "2019");
            ioUtils.verifyStructureInModel(novaEstrutura);

            processar(files, OrdemBusca.ID,null,1);
            //processar(files, OrdemBusca.CNPJ,null,1);

            removerVazios(path);
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
    private void mover(Path arquivo, Path pathCli) throws IOException{
        Path novoArquivo = novaEstrutura.resolve(arquivo.getFileName());
        Path arquivoFinal = pathCli.resolve(novoArquivo);
        if(Files.notExists(arquivoFinal.getParent())) Files.createDirectories(arquivoFinal.getParent());
        Files.move(arquivo, arquivoFinal, StandardCopyOption.REPLACE_EXISTING);
        System.out.println(arquivo + "\t>\t" + arquivoFinal);
        salvarRelatorio(arquivo.toString(),arquivoFinal.toString());
    }
    private void removerVazios(Path path) throws IOException{
        if(Files.isDirectory(path)){
            System.out.println(Files.list(path).count());
            try {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        //System.out.println("delete file: " + file.toString());
                        //Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        if(Files.list(dir).count()==0) {
                            Files.delete(dir);
                            System.out.println("delete dir: " + dir.toString());
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    void salvarRelatorio(String de, String to) throws IOException{
        Path path = Paths.get("result.csv");
        if(Files.notExists(path)) Files.createFile(path);
        FileWriter fw = new FileWriter(path.toFile(),true);
        fw.write(de+"\t"+to);
        fw.write(System.getProperty("line.separator"));
        fw.flush();
        fw.close();
    }

    private Path buscarPorCnpj(Path arquivo, Set<Cliente> clienteSet, Ordem ordem){
        if(arquivo.getFileName().toString().trim().length()<14) return null;
        if(ordem.equals(Ordem.INICIO)) {
            String cnpj = arquivo.getFileName().toString().substring(0,14);
            Optional<Cliente> cliente = clienteSet.stream().filter(c -> c.isCnpjValido() && c.getCnpjFormatado().equals(cnpj)).findAny();
            return cliente.isPresent()? clientIOService.searchClientPathBase(cliente.get()) : null;
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
             return cliente.isPresent() ? clientIOService.searchClientPathBase(cliente.get()) : null;
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
                return cliente.isPresent() ? clientIOService.searchClientPathBase(cliente.get()) : null;
            }
            else return null;
         }
    }
}
