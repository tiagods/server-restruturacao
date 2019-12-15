package com.prolink.olders.dao;

import com.prolink.model.Cliente;

import java.util.List;

public interface DAO {
    List<?> listar();

    List<Cliente> refreshClientes();
}
