import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Arquivo {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("D:\\Arquivo");
        Files.walk(path).filter(Files::isRegularFile).forEach(System.out::println);
    }
}
