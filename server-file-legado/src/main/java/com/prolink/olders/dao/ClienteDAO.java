package com.prolink.olders.dao;

import com.prolink.olders.factory.Conexao;
import com.prolink.olders.model.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.*;

public class ClienteDAO implements DAO {

    private Conexao conexao = new Conexao();

    @Override
    public List<Cliente> listar(){
        return new ArrayList<>();
    }
    @Override
    public List<Cliente> refreshClientes(){
        List<Cliente> clientes = new ArrayList<>();
        Connection con = null;
        try {
            Set<Cliente> clienteSet = new HashSet<>();
            String sql = "select COD,EMPRESA, STATUS, CNPJ from CLIENTE order by COD";
            con = conexao.getCon();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Cliente c = new Cliente();
                c.setId(rs.getInt(1));
                c.setNome(novoNome(rs.getString(2)).toUpperCase());
                c.setStatus(rs.getString(3));
                String cnpj = rs.getString(4);
                c.setCnpj(cnpj==null?"":cnpj);
                clienteSet.add(c);
            }
            clientes.addAll(clienteSet);
            Collections.sort(clientes,Comparator.comparingInt(Cliente::getId));
            return clientes;
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }finally {
            try {
                if(con!=null) con.close();
            }catch (SQLException e){
            }
        }
    }
    private String novoNome(String nome){
        String novo= Normalizer.normalize(nome, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
        return novo;
    }

}
