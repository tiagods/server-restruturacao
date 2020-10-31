package com.tiagods.toolgfip.services;

import com.tiagods.toolgfip.model.ArquivoGfip;
import com.tiagods.toolgfip.model.ChaveGfip;
import com.tiagods.toolgfip.model.Cliente;
import com.tiagods.toolgfip.repository.ArquivoGfipRepository;
import com.tiagods.toolgfip.repository.ChaveGfipRepository;
import com.tiagods.toolgfip.repository.ClienteRepository;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.*;

@Slf4j
@Service
public class GfipService {

    @Autowired
    ClienteRepository clientes;

    @Autowired
    ChaveGfipRepository chaveGfipRepository;

    @Autowired
    ArquivoGfipRepository arquivoGfipRepository;

    public void iniciarProcessamento(String apelido, String cnpj) {
        Observable<String> observable;

        if(StringUtils.hasText(apelido)) {
            observable = Observable.just(apelido)
                    .flatMap(c->
                        c.matches("^[\\d]+$") ? Observable.just(Long.parseLong(apelido)):
                                Observable.error(new Exception("Apelido invalido"))
                    )
                    .flatMap(c-> Observable.just(clientes.findByApelido(c)))
                    .flatMap(c-> c.isPresent() ? Observable.just(c.get().getCnpj()) :
                            Observable.error(new Exception("Cliente não encontrado"))
                    );
        } else {
            observable = Observable.just(cnpj);
        }
        observable
                .flatMap(this::validarCnpj)
                .subscribe(c->
                        processar(apelido, c)
                );
    }

    private void processar(String apelido, String cnpj) {
        Map<String, List<ChaveGfip>> map = pegarChaves(cnpj);
        logarRegistros(map, cnpj);
        List<ChaveGfip> lista = chaveGfipRepository.findAllByCnpj(cnpj);
        lista.stream().forEach(c -> {
            Path file = Paths.get(c.getPath());
            if (Files.exists(file)) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyyHHmmss"));
                Path diretorio = definirDiretorio(file, apelido, timestamp);
                extrairEGerarPdf(c, file, diretorio, apelido);
                percorrerDiretorio(file, apelido, diretorio);
            }
        });
    }

    private Observable<String> validarCnpj(String cnpj) {
        if(StringUtils.hasText(cnpj) && cnpj.matches("^\\d{2}\\.\\d{3}.\\d{3}\\/\\d{4}\\-\\d{2}$")){
            return Observable.just(cnpj);
        }
        return Observable.error(new Exception("Cnpj invalido"));
    }

    private void extrairEGerarPdf(ChaveGfip chaveGfip, Path file, Path diretorio, String apelido) {
        try{
            PDDocument document = PDDocument.load(file.toFile());
            PDDocument documentNew = new PDDocument();
            List<Integer> paginas = chaveGfip.getPaginas();
            for(Integer page : paginas) {
                PDPage pdPage = document.getPage(page-1);
                documentNew.addPage(pdPage);
            }
            //colocando id no nome caso nao exista
            String regex = "(^[\\d]{4})+[^\\d].*";
            String newName = file.getFileName().toString().matches(regex)?
                    file.getFileName().toString():
                    apelido.concat("-").concat(file.getFileName().toString());

            Path newFile = diretorio.resolve(newName);
            documentNew.save(newFile.toFile());
            documentNew.close();
            document.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void logarRegistros(Map<String, List<ChaveGfip>> map, String cnpj) {
        String registros = map.get(cnpj)
                .stream()
                .map(c->c.getPath() + "{"+c.getPaginas()+"}")
                .collect(joining(","));
        log.info("Cnpj: {}. Arquivo:({})", cnpj, registros);
    }

    private Map<String, List<ChaveGfip>> pegarChaves(String cnpj){
        Map<String, List<ChaveGfip>> map = chaveGfipRepository
                .findAllByCnpj(cnpj)
                .stream()
                .collect(groupingBy(ChaveGfip::getCnpj));
        return map;
    }

    private Path definirDiretorio(Path file, String apelido, String timestamep){
        Optional<ArquivoGfip> arquivoGfip = arquivoGfipRepository.findByDiretorio(file.getParent().toString());
        Path diretorio = Paths.get("c:/TEMP", apelido);
        if(arquivoGfip.isPresent()) {
            LocalDate periodo = arquivoGfip.get().getPeriodo();
            if(periodo!=null) {
                String ano = String.valueOf(periodo.getYear());
                String mesValue = String.valueOf(periodo.getMonthValue());

                String mes = mesValue.length() == 2?
                        mesValue : "0".concat(mesValue);

                diretorio = diretorio.resolve(ano);
                diretorio = diretorio.resolve(mes);
            } else {
                diretorio = diretorio.resolve("undefined"+timestamep);
            }

        } else {
            diretorio = diretorio.resolve("undefined"+timestamep);
        }
        return diretorio;
    }

    private void percorrerDiretorio(Path file, String apelido, Path pastaDestino) {
        Path parent = file.getParent();
        //pegando todos os arquivos por chave no parent
        List<ChaveGfip> registrosGfip = chaveGfipRepository.findAllByParent(parent.toString());
        //pega todos os arquivos do diretorio que estao com cnpj
        Map<String, Long> longMap = registrosGfip.stream()
                .filter(filter -> !StringUtils.isEmpty(filter.getCnpj()))
                .collect(groupingBy(ChaveGfip::getPath, counting()));

        try {
            Files.list(parent).filter(Files::isRegularFile).forEach(f -> {
                //validar se o arquivo tem id
                String regex = "(^[\\d]{4})+[^\\d].*";
                boolean comecaPeloId = f.getFileName().toString().matches(regex);
                if (!longMap.keySet().contains(f.toString())) {
                    if (comecaPeloId) {
                        Matcher matcher = Pattern.compile("([\\d]{4})").matcher(f.getFileName().toString());
                        if (matcher.find()) {
                            String idDoNome = matcher.group();
                            if (idDoNome.equals(apelido)) {
                                //se o arquivo nao estiver no path, talvez seja um arquivo compartilhado. Ex: protocolo
                                Path novoArquivo = pastaDestino.resolve(f.getFileName());
                                copiar(f, novoArquivo);
                            }
                        }
                    } else {
                        //se o arquivo nao estiver no path, talvez seja um arquivo compartilhado. Ex: protocolo
                        Path novoArquivo = pastaDestino.resolve(f.getFileName());
                        copiar(f, novoArquivo);
                    }
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void copiar(Path origem, Path destino) {
        try {
            Files.copy(origem, destino);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
