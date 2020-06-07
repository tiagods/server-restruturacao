package com.tiagods.prolink.job;

import com.tiagods.prolink.model.Cliente;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

public interface OperacaoInterface {
    Path mover(Path novaEstrutura, Path arquivo, Path pathCli) throws IOException;

    void removerVazios(Path path) throws IOException;

    //void mover();
    void renomear(Path file, String oldValue, String newValue) throws IOException;
    void addNome(Path path) throws IOException;

    Path buscarIdOrCnpj(Path arquivo, Set<Cliente> clientes, OrdemBusca order, String regex, int index);

    Path buscarPorCnpj(Path arquivo, Set<Cliente> clienteSet, Ordem ordem);

    Path buscarPorId(Path arquivo, Set<Cliente> clienteSet, String regex, int index);
}
