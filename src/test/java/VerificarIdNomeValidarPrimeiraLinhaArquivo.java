import com.prolink.config.BasePath;
import com.prolink.config.ClienteData;
import com.prolink.job.MoverArquivoComPeriodo;
import com.prolink.model.Cliente;
import com.prolink.model.OrdemBusca;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

public class VerificarIdNomeValidarPrimeiraLinhaArquivo extends BasePath {

    public static void main(String[] args){
        new VerificarIdNomeValidarPrimeiraLinhaArquivo().start();
    }
    private void start(){
        try {
            ClienteData data = ClienteData.getInstance();
            Set<Cliente> clienteSet = data.getClientes();
            processar(clienteSet,Paths.get("E:\\Obrigacoes\\contabil\\_old\\SPED PIS COFINS"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void processar(Set<Cliente> clientes,Path path) throws Exception{
        Iterator<Path> ite = Files.list(path).iterator();
        while(ite.hasNext()){
            Path p = ite.next();
            if(Files.isDirectory(p)) processar(clientes,p);
            else {
                String nome = p.getFileName().toString();
                String novoNome = nome.contains(".") ? nome.substring(0, nome.lastIndexOf(".")) : nome;//sped icms ipi
                if (nome.toLowerCase().contains(".txt")) {
                    String id = novoNome.length() >= 4 ? nome.substring(0, 4) : "";
                    if(novoNome.length()>4){
                        try{
                            Integer.parseInt(novoNome.substring(0,5));
                        }catch (Exception e){
                            continue;
                        }
                    }
                    Optional<Cliente> cliente = clientes.stream()
                            .filter(c -> c.getIdFormatado().equals(id))
                            .findAny();
                    if (cliente.isPresent()) {
                        try {
                            Scanner scanner = new Scanner(new FileReader(p.toFile()))
                                    .useDelimiter("\\n");
                            String linha = null;
                            if(scanner.hasNext()){
                                linha = scanner.next();
                            }
                            scanner.close();

                            Path rec = Paths.get(path.toString(), novoNome + ".REC");
                            Path rec2 = Paths.get(path.toString(), novoNome + ".rec");

                            if (linha == null || linha.contains(cliente.get().getCnpjFormatado())) {
                                System.out.println(p);
                                Files.move(p, Paths.get(path.toString(), id + "_" + p.getFileName().toString()));
                                if (Files.exists(rec))
                                    Files.move(rec, Paths.get(path.toString(), id + "_" + rec.getFileName().toString()));
                                else if (Files.exists(rec2))
                                    Files.move(rec2, Paths.get(path.toString(), id + "_" + rec2.getFileName().toString()));


                            }
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }

                }
            }
        }
    }
}
