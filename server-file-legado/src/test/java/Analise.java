import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Analise {
    static StringBuilder builder = new StringBuilder();
    static String lineSeparator = System.getProperty("line.separator");
    public static void main(String[] args) {
        try{
        FileInputStream inputStream = new FileInputStream(new File(".txt"));
        Scanner scanner = new Scanner(inputStream).useDelimiter("\n");
        String[] linha;
        while(scanner.hasNext()){
            linha = scanner.next().split("=");
            String arquivo = linha[0];
            String novoNome = linha[1];
            Path origem = Paths.get(arquivo);
            Path destino = origem.getParent().resolve(novoNome);
            if(Files.exists(destino) && novoNome.equals(origem.getFileName().toString())) {
                System.out.println(destino);
            }
            else if(Files.notExists(origem)){
                Files.move(origem,destino,StandardCopyOption.REPLACE_EXISTING);
            }
        }
        scanner.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        /*
        Path path = Paths.get("\\\\PLKSERVER\\Todos Departamentos\\DeptoPessoal\\GFIP\\CONTIMATIC");
        Set<Path> st = Files.list(path).collect(Collectors.toSet());
        processar(st);

        FileWriter fw = new FileWriter(new File("analise.txt"),true);
        fw.write(builder.toString());
        fw.close();
        */
    }
    public static void processar(Set<Path> pathStream) throws IOException{
        for(Path path : pathStream){
            if(Files.isDirectory(path))  processar(Files.list(path).collect(Collectors.toSet()));
            else builder.append(path+"\t"+path.getFileName()+lineSeparator);
        }
    }
}
