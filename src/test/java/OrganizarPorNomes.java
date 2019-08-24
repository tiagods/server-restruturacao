import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

public class OrganizarPorNomes {

    Path path = Paths.get("E:\\Obrigacoes\\contabil\\_old\\DACON\\DACON 2005\\04");
    String trimestre = "-20054-";

    public static void main(String[] args) {
        new OrganizarPorNomes().iniciar();
    }

    public void iniciar(){
        try {
            voltarParaPastaPai(path.getParent(),true);
            //tirarDaqui("DCTF",Paths.get("E:\\Obrigacoes\\contabil\\_old\\DACON\\DACON 2005"),Paths.get("E:\\Obrigacoes\\contabil\\_old\\DCTF"));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void voltarParaPastaPai(Path pai, boolean limpar) throws IOException {
        Iterator<Path> ite = Files.list(pai).iterator();
        while(ite.hasNext()){
            Path file = ite.next();
            if(Files.isDirectory(file)) {
                if(limpar && Files.list(file).count()==0) Files.delete(file);
                else voltarParaPastaPai(file,limpar);
                continue;
            }
            else{
                if(pai != path) {
                    Path newFile = path.resolve(file.getFileName());
                    Files.move(file, newFile, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println(file + "\t to \t " + newFile);
                }
                else continue;
            }
        }
    }
    public void organizar(String[] condicao, Path pai, boolean limpar) throws IOException {
        Iterator<Path> ite = Files.list(pai).iterator();
        while(ite.hasNext()){
            Path file = ite.next();
            if(Files.isDirectory(file)) continue;
            else{
                boolean habilitar = false;
                for(String s : condicao) {
                    boolean result = file.getFileName().toString().contains(s);
                    if(result) habilitar=true;
                    else{
                        habilitar =false;
                        break;
                    }
                }
                if(habilitar) {
                    Path newFile = path.resolve(file.getFileName());
                    Files.move(file, newFile, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println(file + "\t to \t " + newFile);
                }
                else continue;
            }
        }
    }
    public void tirarDaqui(String condicao, Path de , Path para) throws IOException {
        Iterator<Path> ite = Files.list(de).iterator();
        while(ite.hasNext()){
            Path file = ite.next();
            if(Files.isDirectory(file)) continue;
            else{
                if(file.getFileName().toString().contains(condicao)) {
                    Path newFile = para.resolve(file.getFileName());
                    Files.move(file, newFile, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println(file + "\t to \t " + newFile);
                }
                else continue;
            }
        }
    }
}
