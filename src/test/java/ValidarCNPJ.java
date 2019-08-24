import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidarCNPJ {
    public static void main(String[] args) {
        String cnpj = "04.110.394/0001-91";

        String cnpjFormat = "(^\\d{2}\\d{3}\\d{3}\\d{4}\\d{2}$)";
        cnpj = cnpj.replace(".","").replace("/","").replace("-","");
        Matcher matcher = Pattern.compile(cnpjFormat).matcher(cnpj);
        if(matcher.find()){
            System.out.print("CNPJ valido");
        }
        else
            System.out.print("CNPJ invalido");
    }
}
