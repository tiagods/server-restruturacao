package com.tiagods.clientesender.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;

@Data
@Entity
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COD")
    Long idCliente;
    @Transient
    String apelido;
    @Column(name = "EMPRESA")
    String nome;
    @Column(name = "EMAIL_SOC_1")
    String email;

    public String getApelido() {
        String value = String.valueOf(this.idCliente);
        String newValue = StringUtils.leftPad(value, 4, "0");
        return newValue;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "idCliente=" + idCliente +
                ", apelido='" + getApelido() + '\'' +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
