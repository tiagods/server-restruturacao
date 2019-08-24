import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TesteNome {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("E:\\Obrigacoes\\contabil\\_old - obrigações de anos anteriores 2014\\DIRF 2012\\Dirf\\DIRF 2000.txt");
        System.out.println(path.getFileName().toString().split(" ")[1]);

        System.out.println(path.getFileName().toString().split("\\.")[1]);

        String nome = path.getFileName().toString();
        String novoNome = nome.substring(0,nome.lastIndexOf("."));
        System.out.println(nome.contains("."));

    }
}
