import com.prolink.olders.config.ClienteData;
import com.prolink.olders.model.Cliente;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

public class PDFLeitor {
    public static class PDPageIndex {
        private int value = -1;
        private PDPage pdPage;
        public PDPageIndex(PDPage pdPage, int value){
            this.pdPage=pdPage;
            this.value=value;
        }
        public int getValue() {
            return value;
        }
        public PDPage getPdPage() {
            return pdPage;
        }
    }

    public static void main(String[] args) throws IOException {
        ClienteData clientes = ClienteData.getInstance();
        Path origem = Paths
                .get("\\\\plkserver\\Todos Departamentos\\DeptoPessoal\\GFIP\\CONTIMATIC\\2014\\SIMPLES MISTA - 4005\\03");
        String periodo = "032014";

//        String[] copias = new String[]{"Protocolo.pdf","SEFIPJpkc0ZeUybg00000.xml"};
        String[] copias = new String[]{"PROTOCOLO.pdf"};
//        String[] copias = new String[]{"Protocolo.pdf","SEFIPBKP.ZIP"};

        Map<Path,String> arquivo = new HashMap();
        arquivo.put(Paths.get(origem.toString(),"Demonstrativo das Contribuições.pdf"),"DEMONSTRATIVO DAS CONTRIBUIÇÕES.pdf");
        arquivo.put(Paths.get(origem.toString(),"Relatório RE.pdf"),"RELATORIO RE.pdf");
//        arquivo.put(Paths.get(origem.toString(),"Demonstrativo.pdf"),"DEMONSTRATIVO.pdf");
        for(Path original : arquivo.keySet()) {
            Map<Cliente, Set<PDPageIndex>> map = new HashMap<>();
            PDDocument document = null;
            try {
                document = PDDocument.load(original.toFile());
                PDPageTree tree = document.getPages();
                Iterator<PDPage> iterator = tree.iterator();
                int page = 1;
                while (iterator.hasNext()) {
                    PDPage pdPage = iterator.next();
                    PDFTextStripper stripper = new PDFTextStripper();
                    stripper.setStartPage(page);
                    stripper.setEndPage(page);
                    String text = stripper.getText(document);
                    Optional<Cliente> cli = clientes.getClientes()
                            .stream()
                            .filter(c -> c.isCnpjValido() && text.contains(c.getCnpj()))
                            .findFirst();
                    if (cli.isPresent()) {
                        Set<PDPageIndex> newSet = new HashSet<>();
                        PDPageIndex pageIndex =new PDPageIndex(pdPage,page);
                        if (map.containsKey(cli.get())) {
                            newSet = map.get(cli.get());
                            newSet.add(pageIndex);
                            map.put(cli.get(), newSet);
                        } else {
                            newSet.add(pageIndex);
                            map.put(cli.get(), newSet);
                        }
                    } else {
                        System.out.println("Nenhum encontrado na pagina " + page);
                    }
                    page++;
                }
                Set<Cliente> clienteSet = map.keySet();
                for (Cliente cliente : clienteSet) {
                    PDDocument newDocument = new PDDocument();
                    List<PDPageIndex> list = map.get(cliente).stream().collect(Collectors.toList());

                    Comparator<PDPageIndex> comparator = Comparator.comparingInt(PDPageIndex::getValue);
                    Collections.sort(list,comparator);

                    for (PDPageIndex p : list) {
                        newDocument.addPage(p.getPdPage());
                    }
                    Path path = origem.resolve(cliente.getIdFormatado() +
                            "_"+periodo+"_"+
                            cliente.getCnpjFormatado() +
                            "-" + cliente.getNomeFormatado() +
                            "-" + arquivo.get(original));
                    System.out.println(path);
                    newDocument.save(path.toFile());
                    for(String string : copias){
                        Path copia = origem.resolve(string);
                        String novoNome = cliente.getIdFormatado()+"_"+periodo+"_"+string;
                        Path para = Paths.get(origem.toString(),novoNome.toUpperCase());
                        Files.copy(copia,para,StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                document.close();
            }
        }
    }
}
