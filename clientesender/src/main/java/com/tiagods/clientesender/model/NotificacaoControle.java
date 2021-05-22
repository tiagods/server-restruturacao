package com.tiagods.clientesender.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificacaoControle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long clienteId;
    String processo;
    int maxEnvios = 1;
    int numEnvios = 1;
    @Temporal(TemporalType.DATE)
    Calendar data;
    boolean continuarEnvios = true;

    @PrePersist @PreUpdate
    void updateDate() {
        data = Calendar.getInstance();
    }

    public String getDataFormatada(){
        return data==null? null: new SimpleDateFormat("dd/MM/yyyy").format(data.getTime());
    }

    @Override
    public String toString() {
        return "NotificacaoControle{" +
                "id=" + id +
                ", clienteId=" + clienteId +
                ", processo='" + processo + '\'' +
                ", maxEnvios=" + maxEnvios +
                ", numEnvios=" + numEnvios +
                ", date=" + getDataFormatada() +
                ", continuarEnvios=" + continuarEnvios +
                '}';
    }
}
