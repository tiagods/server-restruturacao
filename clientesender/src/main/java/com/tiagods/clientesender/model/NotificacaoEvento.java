package com.tiagods.clientesender.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacaoEvento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long controleId;
    @Temporal(TemporalType.TIMESTAMP)
    Calendar data;
    Long clienteId;
    String de;
    String para;
    String cc;
    String cco;

    String assunto;
    @Column(columnDefinition = "text")
    String texto;
    String anexos;

    String processo;

    boolean enviado = true;
    int envio = 0;

    @PrePersist
    void prePersist() {
        data = Calendar.getInstance();
    }

    public NotificacaoEvento(Long controleId, Cliente cliente, String processo, EmailDto emailDto) {
        this.controleId = controleId;
        this.clienteId = cliente.getIdCliente();
        this.de = emailDto.getDe();
        this.para = emailDto.getPara() == null ? "" : Arrays.stream(emailDto.getPara()).collect(Collectors.joining(";"));
        this.cc = emailDto.getCc() == null ? "" : Arrays.stream(emailDto.getCc()).collect(Collectors.joining(";"));
        this.cco = emailDto.getBcc()==null? "" : Arrays.stream(emailDto.getBcc()).collect(Collectors.joining(";"));
        this.assunto = emailDto.getAssunto();
        this.texto = emailDto.getEmailedMessage();

        List<String> arquivos = emailDto.getAttachs()
                .values()
                .stream()
                .map(Path::toString)
                .collect(Collectors.toList());

        if(StringUtils.hasText(emailDto.getOutrosAnexos())) {
            String[] anexos = emailDto.getOutrosAnexos().split(";");
            for(String s : anexos) {
                arquivos.add(s.trim());
            }
        }

        String[] newAnexos = new String[arquivos.size()];
        for(int i = 0; i < arquivos.size(); i++){
            newAnexos[i] = arquivos.get(i);
        }

        this.anexos = Arrays.stream(newAnexos).collect(Collectors.joining(";"));
        this.processo = processo;
    }
}
