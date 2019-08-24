package sample;

import com.prolink.config.BasePath;
import com.prolink.job.OperacaoInterface;
import com.prolink.model.Cliente;
import com.prolink.model.Ordem;
import com.prolink.model.OrdemBusca;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

public abstract class Operacao extends BasePath implements OperacaoInterface {

    @Override
    public void addNome(Path origem) throws IOException {
        Iterator<Path> ite = Files.list(origem).iterator();
        while(ite.hasNext()){
            Path path = ite.next();
            String nomeAnterior = path.getFileName().toString();
            int size = 0;
            for (int i = 0; i < nomeAnterior.length(); i++) {
                try {
                    Integer.parseInt(String.valueOf(nomeAnterior.charAt(i)));
                    size++;
                } catch (NumberFormatException e) {//em caso de erro nao fazer nada
                    break;
                }
            }
            if (size > 0 && size < 4) {
                String novoNome = size == 1 ? "000" : (size == 2 ? "00" : (size == 3 ? "0" : ""));
                Path path2 = Paths.get(origem.toString(), novoNome + nomeAnterior);
                Files.move(path, path2, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    @Override
    public Path buscarIdOrCnpj(Path arquivo, Set<Cliente> clientes, OrdemBusca order, String regex, int index){
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
            return cliente.isPresent() ? buscarCliente(cliente.get()) : null;
        }
        else if(order.equals((OrdemBusca.CNPJ))){
            Optional<Cliente> cliente = clientes.stream().filter(c -> c.isCnpjValido() && c.getCnpjFormatado().equals(result)).findAny();
            return cliente.isPresent() ? buscarCliente(cliente.get()) : null;
        }
        return null;
    }


    @Override
    public Path buscarPorCnpj(Path arquivo, Set<Cliente> clienteSet, Ordem ordem) {
        if (arquivo.getFileName().toString().trim().length() < 14) return null;
        if (ordem.equals(Ordem.INICIO)) {
            String cnpj = arquivo.getFileName().toString().substring(0, 14);
            Optional<Cliente> cliente = clienteSet.stream().filter(c -> c.isCnpjValido() && c.getCnpjFormatado().equals(cnpj)).findAny();
            return cliente.isPresent() ? buscarCliente(cliente.get()) : null;
        } else return null;
    }

    @Override
    public Path buscarPorId(Path arquivo, Set<Cliente> clienteSet, String regex, int index) {
        if (arquivo.getFileName().toString().trim().length() < 4) return null;

        if (regex == null) {
            try {
                Integer.parseInt(arquivo.getFileName().toString().substring(0, 5));
                return null;//retornar se id for maior que 4
            } catch (Exception e) {//nao fazer nada
            }
            String valor = arquivo.getFileName().toString().substring(0, 4);
            Optional<Cliente> cliente = clienteSet.stream().filter(c -> c.getIdFormatado().equals(valor)).findAny();
            return cliente.isPresent() ? buscarCliente(cliente.get()) : null;
        } else {
            String nome = arquivo.getFileName().toString();
            String novoNome = nome.contains(".") ? nome.substring(0, nome.lastIndexOf(".")) : nome;
            String[] array = (novoNome).split(regex);
            if (array.length > index && array[index].length() >= 4) {
                try {
                    if (array[index].length() > 4) {
                        Integer.parseInt(array[index].substring(0, 5));
                        return null;//retornar se id for maior que 4
                    }
                } catch (Exception e) {
                    //nao fazer nada
                }
                Optional<Cliente> cliente = clienteSet.stream().filter(c -> c.getIdFormatado().equals(array[index])).findAny();
                return cliente.isPresent() ? buscarCliente(cliente.get()) : null;
            } else return null;
        }

    }

    @Override
    public Path mover(Path novaEstrutura, Path arquivo, Path pathCli) {
        try {
            Path novoArquivo = novaEstrutura.resolve(arquivo.getFileName());
            Path arquivoFinal = pathCli.resolve(novoArquivo);
            if (Files.notExists(arquivoFinal.getParent())) Files.createDirectories(arquivoFinal.getParent());
            Files.move(arquivo, arquivoFinal, StandardCopyOption.REPLACE_EXISTING);
            return arquivoFinal;
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void removerVazios(Path path) {
        try {
            Iterator<Path> files = Files.list(path).iterator();
            while (files.hasNext()) {
                Path f = files.next();
                if (Files.isDirectory(f) && Files.list(f).count() == 0) Files.delete(f);
                else removerVazios(f);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
//
//        if (Files.isDirectory(path)) {
//            try {
//                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
//                    @Override
//                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
//                        return FileVisitResult.CONTINUE;
//                    }
//
//                    @Override
//                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException{
//                        if (Files.list(dir).count() == 0) Files.delete(dir);
//                        return FileVisitResult.CONTINUE;
//                    }
//                });
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public void renomear(Path origem, String oldValue, String newValue) throws IOException {
            File[] files = origem.toFile().listFiles();
            for(File f : files) {
                Path file = f.toPath();
                Path destino = origem.resolve(file.getFileName().toString().replace(oldValue, newValue));
                Files.move(file, destino);
            }
    }
}
