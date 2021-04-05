package com.tiagods.clientes.dao;

import com.tiagods.clientes.model.DataResult;
import com.tiagods.clientes.factory.Factory;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ClientesDAO {

    List<List<Object>> clientes = new ArrayList();
    List<String> colunas = new LinkedList<>();
    Connection con;

    public List<String> obterColunasDoBanco(){
        try {
                String sqlColunas = "SELECT string_agg (column_name, ',') pos " +
                                    "FROM information_schema.columns " +
                                    "WHERE table_schema = 'public' " +
                                    "AND table_name = 'cliente';";

                con = Factory.getConnection();
                PreparedStatement ps = con.prepareStatement(sqlColunas);
                ResultSet rs = ps.executeQuery();

                List<String> colunasExistentes = new ArrayList<>();
                if (rs.next()) {
                    String array = rs.getString(1);
                    colunasExistentes = Arrays.asList(array.split(","));
                }
                return colunasExistentes;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        } finally {
            Factory.fechar(con);
        }
    }

    public DataResult listarClientes (List<String> colunasOrdem) {
        if(clientes.isEmpty()) {
            colunas.clear();

            try {
                String sql = "SELECT * FROM cliente ORDER BY cod";

                if(colunasOrdem!=null && !colunasOrdem.isEmpty()) {
                    String colums = colunasOrdem.stream()
                            .collect(Collectors.joining(",\n"));
                    sql = String.format("SELECT \n %s \nFROM cliente \nORDER BY cod", colums);
                }
                System.out.println("Executando query: "+ sql);

                con = Factory.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                int columnCount = rs.getMetaData().getColumnCount();

                for (int i = 1 ; i <= columnCount ; i++) {
                    colunas.add(rs.getMetaData().getColumnName(i));
                }

                while (rs.next()) {
                    List<Object> row = new ArrayList<>();
                    for (int i = 1 ; i <= columnCount ; i++) {
                        row.add(rs.getObject(i));
                    }
                    clientes.add(row);
                }

                System.out.println(String.format("Total de colunas %d, total de clientes %d", colunas.size(), clientes.size()));
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            } finally {
                Factory.fechar(con);
            }
        }
        return new DataResult(colunas, clientes);
    }
}
