import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Regcao {
    private final String numberApelido = "[0-9]{4}";

    public static class ClienteRegcao{
        private String id;
        private Path path;

        public ClienteRegcao(String id, Path path){
            final String numberApelido = "[0-9]{4}";

            try{
                String name = path.getFileName().toString();

            }catch (NumberFormatException e){
            }
        }
    }

    public static void main(String[] args)  throws IOException {

        Path path = Paths.get("\\\\plkserver\\Todos Departamentos\\Reg_cao\\Scanneados");

        Set<Path> files = Files.list(path.getParent()).collect(Collectors.toSet());
        for(Path p : files) {

        }
    }
}
