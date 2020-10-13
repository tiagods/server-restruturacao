package com.tiagods.gfip.services;

import com.tiagods.gfip.repository.ChaveRepository;
import com.tiagods.gfip.config.ServerFile;
import com.tiagods.gfip.model.Arquivo;
import com.tiagods.gfip.model.Chave;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.collections.list.TreeList;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MapearGfip {

    @Autowired ServerFile serverFile;
    @Autowired ChaveRepository chaveRepository;
    @Autowired ArquivoDAO arquivoDAO;
    @Autowired ChaveDAO chaveDAO;

    Map<Arquivo, List<Chave>> toSave = new ConcurrentHashMap<>();

    boolean processoRodando = false;

    public boolean isProcessoRodando() {
        return (processoRodando && !toSave.isEmpty());
    }

    public void iniciarMapeamento() {
        processoRodando = true;

        String cid = UUID.randomUUID().toString();
        log.info("Correlation: [{}]. Iniciando mapeamento de arquivos gfip", cid);
        Path path = Paths.get(serverFile.getGfip());

//        List<CompletableFuture> futuresList = new ArrayList<>();

//        CompletableFuture save = CompletableFuture.runAsync(()->runSave(cid),
//                CompletableFuture.delayedExecutor(30L, TimeUnit.SECONDS));
//        futuresList.add(future1);

        Path path1 = path.resolve("SEM SISTEMA");
        Path path2 = path.resolve("CONTROL");
        Path path3 = path.resolve("CONTIMATIC");

        CompletableFuture.supplyAsync(()-> runSave(cid));
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(()->processarAssincrono(path1));
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(()->processarAssincrono(path2));
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(()->processarAssincrono(path3));

        CompletableFuture completableFuture = CompletableFuture.allOf(future1, future2, future3);
        if(completableFuture.isDone()) {
            log.info("Correlation: [{}]. Concluindo arquivos gfip", cid);
            processoRodando = false;

        }
    }

    @Async
    private String runSave(String cid) {
        System.out.printf("calling MyBean#runTask() thread: %s%n",
                Thread.currentThread().getName());
        while(processoRodando || !toSave.isEmpty()) {
            if(processoRodando) {
                try {
                    Thread.sleep(10000);
                }catch (InterruptedException e){}
            }
            if(!toSave.isEmpty()){
                Map.Entry<Arquivo, List<Chave>> entry = toSave.entrySet().iterator().next();
                Arquivo arquivo = entry.getKey();
                log.info("Correlation: [{}]. Salvando chaves de ({}) e removendo do map", cid, arquivo.getDiretorio());
                arquivoDAO.salvar(arquivo);
                chaveDAO.salvar(entry.getValue());
                toSave.remove(arquivo);
            }
        }
        return "ToSave";
    }

    @Async
    public String processarAssincrono(Path diretorio){
        if(Files.exists(diretorio) && Files.isDirectory(diretorio)) {
            processarPastas(diretorio);
        }
        return diretorio.getFileName().toString();
    }

    private void processarPastas(Path diretorio) {
        Arquivo arquivo = Arquivo.builder()
                .diretorio(diretorio.toString())
                .data(new Date())
                .build();
        try {
            List<Path> files = Files.list(diretorio).collect(Collectors.toList());
            List<Chave> chaves = new ArrayList<>();
            for (Path file : files) {
                if(Files.isDirectory(file)) {
                    processarPastas(file);
                } else if(file.getFileName().toString().toUpperCase().endsWith(".PDF")){
                    chaves.addAll(processarPdf(file));
                }
            }
            coletarData(chaves).ifPresent(result -> arquivo.setPeriodo(result));
            toSave.put(arquivo, chaves);
        } catch (IOException e) {
            log.error("Falha ao processar pasta: ({}), ex: ({})", diretorio, e.getMessage());
        }
    }

    Optional<LocalDate> coletarData(List<Chave> chaves) {
        if(chaves.isEmpty()) return Optional.empty();

        Map<LocalDate, Long> collect = chaves.stream().filter(c -> c.getPeriodo() != null)
                .collect(Collectors.groupingBy(Chave::getPeriodo, Collectors.counting()));
        Long count = collect.values().stream().max(Long::compareTo).orElse(0L);

        log.info("Lista de datas: ({})", collect);
        log.info("Total de datas encontradas: {}", collect.size());

        Optional<LocalDate> max = collect
                .entrySet()
                .stream().filter(p -> p.getValue().longValue() == count)
                .map(c -> c.getKey())
                .max(LocalDate::compareTo);
        return max;
    }

    private List<Chave> processarPdf(Path pdf) {
        PDDocument document = null;
        List<Chave> chaves = new ArrayList<>();
        try {
            document = PDDocument.load(pdf.toFile());
            for (int i = 1; i <= document.getNumberOfPages(); i++) {
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                String texto = stripper.getText(document);
                if (i == 1 && isProtocolo(texto, pdf)) break;

                final int page = i;

                if (!texto.isEmpty()) {
                    Chave result = buscarCnpjNaPagina(pdf, i, texto);
                    chaves.stream()
                            .filter(f-> f.getPath().equals(result.getPath())
                                    && f.getCnpj().equals(result.getCnpj())
                                    && f.getCnpjSoNumero().equals(result.getCnpjSoNumero())
                            )
                            .findFirst()
                            .ifPresentOrElse(chave -> {
                                Set<Integer> paginas = new TreeSet<>();
                                paginas.addAll(result.getPaginas());
                                paginas.add(page);
                                chave.setPaginas(new ArrayList<>(paginas));
                            }, ()-> chaves.add(result));
                }
            }
        } catch (IOException ex) {
            log.error(ex.getMessage());
        } finally {
            try {
                if (document != null) {
                    document.close();
                }
            } catch (IOException ex) {
            }
        }
        return chaves;
    }

    private Chave buscarCnpjNaPagina(Path arquivo, int pagina, String texto) {
        String regexCnpj = "\\s\\d{2}.\\d{3}.\\d{3}\\/\\d{4}-\\d{2}\\s";
        Matcher matcher = Pattern.compile(regexCnpj).matcher(texto);
        Chave chave = Chave.builder()
                .path(arquivo.toString())
                .parent(arquivo.getParent().toString())
                .data(new Date())
                .paginas(Arrays.asList(pagina))
                .build();

        if (matcher.find()) {
            String cnpj = matcher.group().trim();
            String soNumeros = cnpj.replaceAll("[^\\d]", "");
            log.info("Arquivo: {}, Pagina: {}, Cnpj: {}, Apenas Numeros: {}", arquivo.toString(), pagina, cnpj, soNumeros);
            chave.setCnpj(cnpj);
            chave.setCnpjSoNumero(soNumeros);
            chave.setData(new Date());

            Matcher matcherData = Pattern.compile("\\s(\\d{2}\\/\\d{4})\\s").matcher(texto);
            if(matcherData.find()) {
                try {
                    String valor = matcherData.group().trim();
                    valor = "01/"+valor;
                    LocalDate localDate = LocalDate.from(DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            .parse(valor));
                    chave.setPeriodo(localDate);
                }catch (DateTimeParseException ex){}
            }
        }



        return chave;
    }

    private boolean isProtocolo(String texto, Path pdf){
        //verificar se protocolo
        String regexProtocolo = "(?:(\\s?)CONECTIVIDADE SOCIAL(\\s?))(?:(\\s?)Protocolo de Envio de Arquivos(\\s?))";
        Matcher matcher = Pattern.compile(regexProtocolo).matcher(texto);
        if (matcher.find()) {
            log.info("O arquivo é um protocolo: {} ", pdf);
            return true;
        }
        return false;
    }

    private boolean arquivoEmBranco(Path pdf, PDDocument document) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        String texto = stripper.getText(document);
        if (stripper.getText(document).equals("")) {
            log.info("Arquivo em branco");
            try {
                texto = tesseract().doOCR(pdf.toFile());
            } catch (TesseractException e) {
                log.warn("Nao foi possivel executar o tesseract no arquivo: ({})", pdf.toString());
            }
        }
        if (texto.trim().isEmpty()) {
            log.warn("O arquivo {} esta em branco", pdf.toString());
            //arquivosEmBranco.add(pdf, pagina);
            return true;
        } else return false;
    }

    private ITesseract tesseract() {
        ITesseract instance = new Tesseract();
        instance.setLanguage("por");//por,eng
        return instance;
    }
}
