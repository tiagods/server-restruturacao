package com.prolink.factory;

import com.prolink.config.DBConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao extends DBConfig {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public Conexao() {
        super();
    }
    public Connection getCon(){
        try {
            Class.forName(getDBDRIVER());
            return DriverManager.getConnection(getURL(),getUSER(),getPASSWORD());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
