package com.tiagods.obrigacoes.job;

import com.tiagods.obrigacoes.model.Cliente;
import com.tiagods.obrigacoes.service.ClienteService;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;


public class MoverArquivoComPeriodo {

    ClienteService clientIOService;

    private static Set<String> meses = new HashSet<>();

    private Set<Cliente> clienteSet;
    public static void main(String[] args) {
        String[] m = new String[]{"01","02","03","04","05","06","07","08","09","10","11","12"};
        meses.addAll(Arrays.asList(m));
        String cid = UUID.randomUUID().toString();
        new MoverArquivoComPeriodo().iniciar(cid);

        ObrigacaoV1 o = new ObrigacaoV1("Obrigacao/Documento");
        List<ObrigacaoV1> obrigacaoSet = new ArrayList<>();
        obrigacaoSet.add(new ObrigacaoV1("DIRF"));
        obrigacaoSet.add(new ObrigacaoV1("DAS"));
        obrigacaoSet.add(new ObrigacaoV1("GPS"));
        obrigacaoSet.add(new ObrigacaoV1("DIPJ"));
        obrigacaoSet.add(new ObrigacaoV1("DIMOB"));
        obrigacaoSet.add(new ObrigacaoV1("DACON"));
        obrigacaoSet.add(new ObrigacaoV1("RAIS"));
        obrigacaoSet.add(new ObrigacaoV1("DCTF"));
        obrigacaoSet.add(new ObrigacaoV1("SIMPLES PAULISTA"));
        obrigacaoSet.add(new ObrigacaoV1("DES"));
        obrigacaoSet.add(new ObrigacaoV1("PJSI"));
        obrigacaoSet.add(new ObrigacaoV1("DMED"));
        obrigacaoSet.add(new ObrigacaoV1("DITR"));
        obrigacaoSet.add(new ObrigacaoV1("SPED CONTABIL"));
        obrigacaoSet.add(new ObrigacaoV1("PJ INATIVA"));
        obrigacaoSet.add(new ObrigacaoV1("STDA SIMPLES NACIONAL"));
        obrigacaoSet.add(new ObrigacaoV1("COAF"));
        obrigacaoSet.add(new ObrigacaoV1("DSUP"));
        obrigacaoSet.add(new ObrigacaoV1("FCONT"));
        obrigacaoSet.add(new ObrigacaoV1("GIA-ICMS"));
        obrigacaoSet.add(new ObrigacaoV1("IRPF"));
        obrigacaoSet.add(new ObrigacaoV1("PERD COMP"));
        obrigacaoSet.add(new ObrigacaoV1("SISCOSERV"));
        obrigacaoSet.add(new ObrigacaoV1("SPED ICMS IPI"));
        obrigacaoSet.add(new ObrigacaoV1("SPED PIS COFINS"));
        obrigacaoSet.add(new ObrigacaoV1("DAI-PMSP"));
        obrigacaoSet.add(new ObrigacaoV1("ECF CONTABIL"));
        obrigacaoSet.add(new ObrigacaoV1("SEDIF-DESTDA"));
        obrigacaoSet.add(new ObrigacaoV1("IBGE"));
        obrigacaoSet.add(new ObrigacaoV1("SIMPLES NACIONAL-DEFIS"));
        obrigacaoSet.add(new ObrigacaoV1("SIMPLES NACIONAL-PGDASD"));
        obrigacaoSet.add(new ObrigacaoV1("DECLAN-IPM"));

    }
    private void iniciar(String cid) {
        String ano = "2019";
        Path path = Paths.get("\\\\PLKSERVER\\Obrigacoes\\contabil\\Contabil\\SPED ICMS IPI",ano);
        try {
            Iterator<Path> files = Files.list(path).iterator();
            while(files.hasNext()) {
                Path p = files.next();
                if(Files.isDirectory(p) && meses.contains(p.getFileName().toString())) {
                    Path estrutura = Paths.get("Obrigacoes", "SPED ICMS IPI", ano);
                    Path novaEstrutura = estrutura.resolve(p.getFileName());
                    clientIOService.verificarEstruturaNoModelo(novaEstrutura);
                    processar(cid, novaEstrutura,Files.list(p).iterator(), OrdemBusca.CNPJ,null,1);
                    processar(cid, novaEstrutura,Files.list(p).iterator(), OrdemBusca.ID, null, 1);
                    processar(cid, novaEstrutura,Files.list(p).iterator(), OrdemBusca.CNPJ,"-",1);
                    processar(cid, novaEstrutura,Files.list(p).iterator(), OrdemBusca.ID, "_", 1);

                }
                else System.out.println("Ignorando "+ p);
            }

            removerVazios(path);

            System.exit(0);

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void processar(String cid, Path estrutura, Iterator<Path> files, OrdemBusca ordemBusca,String regex,int index) throws IOException{
        while (files.hasNext()) {
            Path arquivo  = files.next();
            if(Files.isDirectory(arquivo)){
                processar(cid, estrutura,Files.list(arquivo).iterator(), ordemBusca,regex,index);
            }
            else {
                Path pathCli = buscarIdOrCnpj(cid, arquivo,clienteSet,ordemBusca,regex,index);
                if(pathCli != null) mover(estrutura,arquivo,pathCli);
//                if(ordemBusca.equals(OrdemBusca.CNPJ)) {
//                    Path pathCli = buscarPorCnpj(arquivo, clienteSet, Ordem.INICIO);
//                    if (pathCli != null) mover(estrutura,arquivo, pathCli);
//                }
//                else{
//                    Path pathCli = buscarPorId(arquivo,clienteSet,regex,index);
//                    if(pathCli !=null) mover(estrutura,arquivo,pathCli);
//                }
            }
        }
    }
    private void mover(Path novaEstrutura, Path arquivo, Path pathCli) throws IOException{
        Path novoArquivo = novaEstrutura.resolve(arquivo.getFileName());
        Path arquivoFinal = pathCli.resolve(novoArquivo);
        if(Files.notExists(arquivoFinal.getParent())) Files.createDirectories(arquivoFinal.getParent());
        Files.move(arquivo, arquivoFinal, StandardCopyOption.REPLACE_EXISTING);
        System.out.println(arquivo + "\t>\t" + arquivoFinal);
        salvarRelatorio(arquivo.toString(),arquivoFinal.toString());
    }
    private void removerVazios(Path path) throws IOException{
        if(Files.isDirectory(path)){
            try {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
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
        fw.write(de+";"+to);
        fw.write(System.getProperty("line.separator"));
        fw.flush();
        fw.close();
    }
    private Path buscarIdOrCnpj(String cid, Path arquivo, Set<Cliente> clientes, OrdemBusca order, String regex, int index){
        int size = order.equals(OrdemBusca.CNPJ)?14:4;//se cnpj = 14 ou se id =4
        String valor = "";
        if (arquivo.getFileName().toString().trim().length() < size) return null;
        if (regex == null) {
            try {
                if (arquivo.getFileName().toString().length() > size) {
                    Integer.parseInt(arquivo.getFileName().toString().substring(0, size + 1));
                    return null;//retornar se id for maior que o valor
                }
            } catch (Exception e) {//nao fazer nada
            }
            valor = arquivo.getFileName().toString().substring(0, size);
        }
        else {
            String nome = arquivo.getFileName().toString();
            String novoNome = nome.contains(".") ? nome.substring(0, nome.lastIndexOf(".")) : nome;
            String[] array = (novoNome).split(regex);
            if (array.length > index && array[index].length() >= size) {

                try {
                    if (array[index].length() > size) {
                        Integer.parseInt(array[index].substring(0, size + 1));
                        return null;//retornar se id for maior que 4
                    }
                } catch (Exception e) {
                    //nao fazer nada
                }
                valor = array[index].substring(0,size);
            }
        }
        final String result = valor;
        if(order.equals(OrdemBusca.ID)) {
            Optional<Cliente> cliente = clientes.stream().filter(c -> c.getIdFormatado().equals(result)).findAny();
            return cliente.isPresent() ? clientIOService.buscarPastaDoClienteECriarSeNaoExistir(cid, cliente.get()) : null;
        }
        else if(order.equals((OrdemBusca.CNPJ))){
            Optional<Cliente> cliente = clientes.stream().filter(c -> c.isCnpjValido() && c.getCnpjFormatado().equals(result)).findAny();
            return cliente.isPresent() ? clientIOService.buscarPastaDoClienteECriarSeNaoExistir(cid, cliente.get()) : null;
        }
        return null;
    }
    private Path buscarPorCnpj(String cid, Path arquivo, Set<Cliente> clienteSet, OrdemV1 ordemV1){
        if(arquivo.getFileName().toString().trim().length()<14) return null;
        if(ordemV1.equals(OrdemV1.INICIO)) {
            String cnpj = arquivo.getFileName().toString().substring(0,14);
            Optional<Cliente> cliente = clienteSet.stream().filter(c -> c.isCnpjValido() && c.getCnpjFormatado().equals(cnpj)).findAny();
            return cliente.isPresent()? clientIOService.buscarPastaDoClienteECriarSeNaoExistir(cid, cliente.get()) : null;
        }
        else return null;
    }
    private Path buscarPorId(String cid, Path arquivo, Set<Cliente> clienteSet,String regex,int index){
        if(arquivo.getFileName().toString().trim().length()<4) return null;

        if(regex==null){
            try{
                Integer.parseInt(arquivo.getFileName().toString().substring(0,5));
                return null;//retornar se id for maior que 4
            }catch (Exception e) {//nao fazer nada
            }
            String valor =  arquivo.getFileName().toString().substring(0,4);
            Optional<Cliente> cliente = clienteSet.stream().filter(c -> c.getIdFormatado().equals(valor)).findAny();
            return cliente.isPresent() ? clientIOService.buscarPastaDoClienteECriarSeNaoExistir(cid, cliente.get()) : null;
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
                return cliente.isPresent() ? clientIOService.buscarPastaDoClienteECriarSeNaoExistir(cid, cliente.get()) : null;
            }
            else return null;
        }
    }
}
