package com.tiagods.gfip.services;

import com.tiagods.gfip.repository.ArquivoRepository;
import com.tiagods.gfip.repository.ChaveRepository;
import com.tiagods.gfip.config.ServerFile;
import com.tiagods.gfip.model.Arquivo;
import com.tiagods.gfip.model.Chave;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MapearGfip {

    @Autowired
    ServerFile serverFile;
    @Autowired
    ChaveRepository chaveRepository;

    @Autowired
    ArquivoRepository arquivoRepository;

    Set<Arquivo> arquivos = new HashSet<>();
    Set<Path> arquivosEmBranco = new HashSet<>();
    Map<Path, Set<Path>> arquivosDaPasta = new HashMap<>();//sao arquivos que não tem clientes

    @Getter boolean processoRodando = false;

    public synchronized void iniciarMapeamento() {
        processoRodando = true;
        String cid = UUID.randomUUID().toString();
        log.info("Correlation: [{}]. Iniciando mapeamento de arquivos gfip", cid);

        chaveRepository.deleteAll();
        arquivoRepository.deleteAll();

        Path path = Paths.get(serverFile.getGfip());
        if (Files.exists(path) && Files.isDirectory(path)) {
            processarPastas(path);
        } else {
            log.info("Correlation: [{}]. Pasta nao existe ({})", cid, path.toString());
        }

        List<Chave> chaves = arquivos.stream()
                .flatMap(arquivo -> arquivo.getChaves().stream())
                //.filter(f -> f.getCnpj().equals(clienteCnpj) && f.getCnpjSoNumero().equals(soNumeros))
                .collect(Collectors.toList());


        chaves.forEach(chave -> {
            chaveRepository.save(chave);
//            log.info("O cliente de cnpj: {}, foi encontrado no arquivo: {}, paginas: ({})", clienteCnpj, chave.getArquivo().getArquivo().toString(), chave.getPaginas());
        });
        log.info("Correlation: [{}]. Concluindo arquivos gfip", cid);
        processoRodando = false;
    }

    private void processarPastas(Path diretorio) {
        arquivosDaPasta.put(diretorio, new HashSet<>());
        try {
            List<Path> files = Files.list(diretorio).collect(Collectors.toList());
            for (Path file : files) {
                if (!Files.isDirectory(file)) {
                    analisarArquivo(diretorio, file);
                } else {
                    processarPastas(file);
                }
            }
        } catch (IOException e) {
            log.error("Falha ao processar pasta: ({}), ex: ({})", diretorio, e.getMessage());
        }
    }

    private void analisarArquivo(Path diretorio, Path arquivo) {
        if (arquivo.getFileName().toString().toUpperCase().endsWith(".PDF")) {
            //ignorar arquivos pdf que nao devem ser processados
            if (!arquivosDaPasta.get(diretorio).contains(arquivo)) {
                processarPdf(arquivo);
            }
        } else {
            arquivosDaPasta.get(diretorio).add(arquivo);
        }
    }

    private void processarPdf(Path pdf) {
        PDDocument document = null;
        try {
            document = PDDocument.load(pdf.toFile());
            if (!arquivoEmBranco(pdf, document)) {
                processarPdfPorPagina(document, pdf);
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
    }

    private void processarPdfPorPagina(PDDocument document, Path pdf) throws IOException{
        for (int i = 1; i <= document.getNumberOfPages(); i++) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setStartPage(i);
            stripper.setEndPage(i);
            String texto = stripper.getText(document);
            if (i == 1 && isProtocolo(texto, pdf)) break;
            if (!texto.isEmpty()) {
                buscarCnpjNaPagina(pdf, i, texto);
            }
        }
    }

    private boolean isProtocolo(String texto, Path pdf){
        //verificar se protocolo
        String regexProtocolo = "(?:(\\s?)CONECTIVIDADE SOCIAL(\\s?))(?:(\\s?)Protocolo de Envio de Arquivos(\\s?))";
        Matcher matcher = Pattern.compile(regexProtocolo).matcher(texto);
        if (matcher.find()) {
            log.info("O arquivo é um protocolo: {} ", pdf);
            arquivosDaPasta.get(pdf.getParent()).add(pdf);
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
            arquivosEmBranco.add(pdf);
            return true;
        } else return false;
    }

    private ITesseract tesseract() {
        ITesseract instance = new Tesseract();
        instance.setLanguage("por");//por,eng
        return instance;
    }

    private void buscarCnpjNaPagina(Path arquivo, int pagina, String texto) {
        String regexCnpj = "\\s\\d{2}.\\d{3}.\\d{3}\\/\\d{4}-\\d{2}\\s";
        Matcher matcher = Pattern.compile(regexCnpj).matcher(texto);
        while (matcher.find()) {
            String cnpj = matcher.group().trim();
            String soNumeros = cnpj.replaceAll("[^\\d]", "");
            log.info("Arquivo: {}, Pagina: {}, Cnpj: {}, Apenas Numeros: {}", arquivo.toString(), pagina, cnpj, soNumeros);
            Optional<Arquivo> optionalArquivo = arquivos.stream()
                    .filter(c -> c.getDiretorio().equals(arquivo.getParent().toString()))
                    .findFirst();

            if (optionalArquivo.isPresent()) {
                Optional<Chave> optionalChave = optionalArquivo.get()
                        .getChaves()
                        .stream()
                        .filter(c -> c.getArquivo().equals(arquivo) && (c.getCnpj().equals(cnpj) || c.getCnpjSoNumero().equals(soNumeros)))
                        .findFirst();
                //se ja existir, vai filtrar por arquivo e adicionar as novas paginas
                if (optionalChave.isPresent()) {
                    optionalChave.get().getPaginas().add(pagina);
                } else {//caso contrario, criar nova chave a adicionar
                    Chave chave = new Chave();
                    chave.setCnpj(cnpj);
                    chave.setCnpjSoNumero(soNumeros);
                    chave.setArquivo(optionalArquivo.get());
                    chave.setPath(arquivo.toString());
                    chave.setData(new Date());
                    chave.setPaginas(Arrays.asList(pagina));
                    optionalArquivo.get().getChaves().add(chave);
                }
            } else {
                Arquivo arq = new Arquivo();
                arq.setDiretorio(arquivo.getParent().toString());
                arq.setChaves(new ArrayList<>());
                arq = arquivoRepository.save(arq);

                Chave chave = new Chave();
                chave.setCnpj(cnpj);
                chave.setPath(arquivo.toString());
                chave.setCnpjSoNumero(soNumeros);
                chave.setArquivo(arq);
                chave.setData(new Date());
                chave.setPaginas(Arrays.asList(pagina));

                arq.getChaves().add(chave);
                arquivos.add(arq);
            }
        }
    }
}
