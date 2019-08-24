import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class TestarCopiaAtt {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("E:\\Clientes\\_modelo\\Contabil");
        Path path1 = Paths.get("E:\\Clientes\\teste\\Contabil");

        //Files.copy(path,path1,StandardCopyOption.COPY_ATTRIBUTES);


        String nome = "EFD01933.TXT";
        String novoNome = nome.contains(".") ? nome.substring(nome.length()-4, nome.lastIndexOf(".")) : nome;//pis confins
        System.out.println(novoNome);
    }
}
