package com.tiagods.clientes.factory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Factory {

    public static Connection getConnection() {
        try {
            Properties properties = new Properties();
            properties.load(Factory.class.getClass().getResourceAsStream("/database.properties"));
            String url = properties.getProperty("url");
            String usuario = properties.getProperty("usuario");
            String senha = properties.getProperty("senha");

            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(url, usuario, senha);
        }catch (ClassNotFoundException | SQLException | IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public static void fechar(Connection connection) {
        try {
            if( connection!= null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
